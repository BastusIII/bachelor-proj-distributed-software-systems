package vss3.aufgabe5.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;

import vss3.aufgabe5.communication.SalesmenCommunicationClient;
import vss3.aufgabe5.communication.content.ShortestPath;
import vss3.aufgabe5.communication.content.TableContent;
import vss3.aufgabe5.communication.content.Task;
import vss3.aufgabe5.communication.content.TaskFinished;
import vss3.aufgabe5.communication.content.TaskRequest;

/**
 * Worker Client
 * 
 * Requests tasks from server and calculates the shortestPath for this task.
 * 
 * @author Waldleitner
 *
 */
public class SalesmenWorker extends Thread implements Observer {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = Logger.getLogger(SalesmenWorker.class);
	/**
	 * Communication manager
	 */
	private final SalesmenCommunicationClient scc;
	/**
	 * Distance table for the cities
	 */
	private int[][] table = null;
	/**
	 * Actual task to calculate
	 */
	private Task task = null;
	/**
	 * Shortest way for a complete round trip who was found.
	 */
	private int minWay = Integer.MAX_VALUE;
	/**
	 * Start way from the task
	 */
	private final ArrayList<Integer> startWay = new ArrayList<Integer>();
	/**
	 * Available cities who can be added to the start way.
	 */
	private final ArrayList<Integer> availableCities = new ArrayList<Integer>();

	/**
	 * Constructor
	 * 
	 * @param serverName	IP-address to the server.
	 */
	public SalesmenWorker(String serverName){
		SalesmenCommunicationClient tmp = null;
		while (tmp == null) {
			try {
				tmp = new SalesmenCommunicationClient(serverName);

			} catch (IOException e) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					LOGGER.error(e);
				}
				LOGGER.error(e);
			}
		}
		scc = tmp;
		scc.addObserver(this);
		LOGGER.info("Successfully connected.");
		this.start();
	}

	/**
	 * Requests new task from the server and initialize all needed variable lists if task is received.
	 */
	private void getNewTask() {
		while ((table == null || task == null)) {
			try {
				scc.sendContent(new TaskRequest());
			} catch (IOException e) {
				LOGGER.error(e);
			}
			synchronized (this) {
				try {
					LOGGER.info("Waiting for table or task.");
                    if (task == null) {
					    this.wait(10000);
                    }
                    LOGGER.info("Waiting for table or task ended.");
				} catch (InterruptedException e) {
					LOGGER.error(e);
				}
			}
		}
		LOGGER.info("Task and table successfully received.");
		for (int i = 0; i < table[0].length; i++) {
			availableCities.add(i);
		}
		LOGGER.debug("Parsing start path");
		for (Integer city : task.getPath()) {
			LOGGER.debug(city);
			startWay.add(city);
			availableCities.remove(city);
		}
		minWay = task.getShortestWayFound();
		LOGGER.info("Parsing startPath and availableCities completed.");
		task = null;
	}

	/**
	 * Resets the lists from the actual task and tells the server that he has finished the task.
	 */
	private void taskFinished() {
		availableCities.clear();
		startWay.clear();
		try {
			scc.sendContent(new TaskFinished());
		} catch (IOException e) {
			LOGGER.error(e);
		}
	}

	@Override
	/**
	 * Main loop for worker.
	 * Requests new Task, initialize the start way, starts to calculate.
	 */
	public void run() {
		while (true) {
			getNewTask();
			LOGGER.info("Start Working");
			ArrayList<Integer> actWay = (ArrayList<Integer>) startWay.clone();
			ArrayList<Integer> actAvailableCities = (ArrayList<Integer>) availableCities.clone();

			int wayLength = 0;
			for (int i = 1; i < actWay.size(); i++) {
				wayLength += getDistance(actWay.get(i-1), actWay.get(i));
			}

			recursiveSearch(wayLength, actWay, actAvailableCities);
			taskFinished();
			LOGGER.info("Finished Working");
		}
	}

	/**
	 * Recursive search after the shortest way in the task.
	 * 
	 * @param wayLength				Actual distance in the path.
	 * @param actWay				Actual path.
	 * @param actAvailableCities	Actual available cities for next step.
	 */
	private void recursiveSearch(int wayLength, ArrayList<Integer> actWay, ArrayList<Integer> actAvailableCities) {

		/**
		 * Handling for completed paths
		 */
		if (actAvailableCities.size() == 0) {
			Integer firstCity = actWay.get(0);
			wayLength += getDistance(actWay.get(actWay.size() - 1), firstCity);
			actWay.add(firstCity);
			
			if (wayLength < minWay) {
				LOGGER.debug("Found shortest Path with length: "+wayLength);
				ShortestPath shortestPath = new ShortestPath();
				shortestPath.setPathLength(wayLength);
				shortestPath.setPath(actWay);
				//Send the new shortest Path to the server
				try {
					scc.sendContent(shortestPath);
				} catch (IOException e) {
					LOGGER.error(e);
				}
				synchronized(this){
					if(wayLength < minWay){
						minWay = wayLength;						
					}
				}
			}
		}
		else {
			for (Integer actCity : actAvailableCities) {
				int nextWayLength = wayLength + getDistance(actWay.get(actWay.size() - 1), actCity);
				
				// If the actual distance of the path is longer then the shortest path we can cut the search here 
				//	because there is no better solution in this tree.
				if (nextWayLength >= minWay) {
					continue;
				}
				ArrayList<Integer> nextWay = (ArrayList<Integer>) actWay.clone();
				ArrayList<Integer> nextAvailableCities = (ArrayList<Integer>) actAvailableCities.clone();
				nextWay.add(actCity);
				nextAvailableCities.remove(actCity);
				recursiveSearch(nextWayLength, nextWay, nextAvailableCities);
			}
		}

	}

	/**
	 * Gets the distance of the cities.
	 * 
	 * @param a	first city
	 * @param b	second city
	 * @return	distance between this cities.
	 */
	private int getDistance(int a, int b) {
		return (table[a][b] == -1) ? table[b][a] : table[a][b];
	}

	@Override
	/**
	 * Message Handling from the server
	 */
	public void update(Observable obj, Object arg) {
		// Updates the shortest way length which was found.
		if (arg instanceof ShortestPath) {
			synchronized (this) {
				ShortestPath shortestPath = (ShortestPath) arg;
				minWay = shortestPath.getPathLength();
				LOGGER.info("Got new shortest Path length: "+minWay);
			}
		}
		//	Sets the new task.
		else if (arg instanceof Task) {
			task = (Task) arg;
			synchronized(this){
				this.notifyAll();
			}
			LOGGER.info("Got new Task.");
		}
		//	Gets the distance table for the cities.
		else if (arg instanceof TableContent) {
			TableContent tableContent = (TableContent) arg;
			table = tableContent.getDistances();
			synchronized(this){
				this.notifyAll();
			}
			LOGGER.info("Got the table.");
		}
	}

}
