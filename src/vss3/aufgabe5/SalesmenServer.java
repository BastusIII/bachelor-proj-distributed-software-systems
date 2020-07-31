package vss3.aufgabe5;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import vss3.aufgabe4.MainMaster;
import vss3.aufgabe5.communication.SalesmenCommunicationMessage;
import vss3.aufgabe5.communication.SalesmenCommunicationServer;
import vss3.aufgabe5.communication.SalesmenCommunicationServerEventLoop;
import vss3.aufgabe5.communication.SalesmenCommunicationServerSocketThreads;
import vss3.aufgabe5.communication.content.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The Salesmen Server is the Master instance, that offers the salesmen problem calculation method.
 *
 * @author Sebastian Stumpf
 * @author Waldleitner
 */
public class SalesmenServer {

	/**
	 * Logger
	 */
    private static final Logger LOGGER = Logger.getLogger(SalesmenServer.class);
    /**
     * All tasks finished?
     */
    private boolean finished;
    /**
     * Distance table for all cities.
     */
    private TableContent tableContent;
    /**
     * Communication manager for the server.
     */
    private SalesmenCommunicationServer communicationServer;
    /**
     * Shortest path which is actual found.
     */
    private List<Integer> shortestPath;
    /**
     * Shortest path length which is actual found.
     */
    private int minimum;
    /**
     * Amount of all tasks.
     */
    private int initialSizeOfTasks;
    /**
     * Amount of all not calcuated tasks.
     */
    private int remainingTasks;
    /**
     * Iterator for all start ways.
     */
    private TaskIterator taskIterator;
    /**
     * Map for all Clients which actually calculate a task.
     */
    private HashMap<Integer, Task> tasksInProcess;

    /**
     * Create an instance of SalesmenServer.
     * @throws IOException if the SalesmenCommunicationServer could not be started.
     */
    public SalesmenServer() throws IOException {

        this.communicationServer = new SalesmenCommunicationServerEventLoop();
        this.shortestPath = null;
        this.taskIterator = null;
        this.tasksInProcess = new HashMap<>();
        this.remainingTasks = 0;
        this.finished = false;
        this.minimum = Integer.MAX_VALUE;
        this.initialSizeOfTasks = 0;

    }

    /**
     * Get readable version of the shortest path.
     * @return the shortest path.
     */
    public String getReadableShortestPath() {

        String path;
        if (shortestPath == null) {
            path = "no calculation yet";
        } else {
            StringBuilder sb = new StringBuilder();
            for (Integer city : shortestPath) {
                sb.append(tableContent.getCity(city));
                sb.append("-->");
            }
            path = sb.substring(0, sb.length() - 3);
        }
        return path;
    }

    /**
     * Get readable version of the starting city.
     * @return the starting city.
     */
    public String getReadableStartCity() {

        return tableContent.getCity(shortestPath.get(0));
    }

    /**
     * Get readable version of minimum.
     * @return the minimum.
     */
    public String getReadableMinimum() {

        return this.minimum == Integer.MAX_VALUE ? "no calculation yet" : Integer.toString(this.minimum);
    }

    /**
     * Reset all entities, so a new calculation can be started.
     */
    public void reset() {
        this.shortestPath = null;
        this.taskIterator = null;
        this.tasksInProcess.clear();
        this.remainingTasks = 0;
        this.finished = false;
        this.minimum = Integer.MAX_VALUE;
        this.initialSizeOfTasks = 0;
    }

    /**
     * Start the traveling salesmen calculation.<br />
     * Status messages are displayed while the calculation is performed.
     * @param workers the amount of available workers.
     * @param city the start city.
     */
    public void startTravelingSalesman(final int workers, final int city, int[] cityIndices) {
        tableContent = TableContent.getTableWithCities(cityIndices);
        //Calculation of maxDepth needed for the amount of workers
        int amountOfCities = tableContent.getDistances()[0].length;
        this.initialSizeOfTasks = 1;
		int maxDepth = 1;
		for(; maxDepth<Integer.MAX_VALUE; maxDepth++){
			if(maxDepth>=amountOfCities-3){
				break;
			}
			initialSizeOfTasks *= amountOfCities-maxDepth;
			if(workers <= initialSizeOfTasks){
				break;
			}
		}
        taskIterator = new TaskIterator(maxDepth,amountOfCities, city);
        this.remainingTasks = initialSizeOfTasks;
        
        
        Thread messageHandler = new Thread() {
            public void run() {
                if(LOGGER.isDebugEnabled()) LOGGER.debug("Message handler thread created.");
                while (!finished) {
                    handleMessage();
                }
            }
        };
        Thread statusLogger = new Thread() {
            public void run() {
                if(LOGGER.isDebugEnabled()) LOGGER.debug("Status logger thread created.");
                while (!finished) {
                    try {
                        sleep(5000);
                    } catch (InterruptedException e) {
                        LOGGER.error("Status logging thread interrupted: " + e.getMessage());
                    }
                    logStatus();
                }
                logStatus();
            }
        };
        messageHandler.start();
        statusLogger.start();
        // waiting for Threads to finish, then return
        try {
            messageHandler.join();
            statusLogger.join();
        } catch (InterruptedException e) {
            LOGGER.error("Interrupted while waiting for threads to finish: " + e.getMessage());
        }
    }

    /**
     * Get and handle a message from the communication server.
     */
    private void handleMessage() {

        SalesmenCommunicationMessage message = communicationServer.getMessage();
        MessageContent content = message.getContent();
        int clientId = message.getAddress();
        try {
            if (content instanceof ShortestPath) {
                LOGGER.info("Got ShortestPath message from: " + clientId + " with path length " + ((ShortestPath) content).getPathLength());
                ShortestPath shortestPath = (ShortestPath) content;
                if (shortestPath.getPathLength() < this.minimum) {
                    this.shortestPath = shortestPath.getPath();
                    this.minimum = shortestPath.getPathLength();
                    this.communicationServer.broadCastContent(content);
                }

            } else if (content instanceof TaskRequest) {

                if(LOGGER.isDebugEnabled()) LOGGER.debug("Got TaskRequest message from: " + clientId);
                if (taskIterator.hasNext() && this.tasksInProcess.get(clientId) == null) {
                    SalesmenCommunicationMessage answerMessageMap = new SalesmenCommunicationMessage(tableContent);
                    answerMessageMap.setAddress(clientId);
                    communicationServer.sendMessage(answerMessageMap);
                    Task task = new Task();
                    task.setPath(taskIterator.next());
                    task.setShortestWayFound(this.minimum);
                    SalesmenCommunicationMessage taskMessage =  new SalesmenCommunicationMessage(task);
                    taskMessage.setAddress(clientId);
                    this.communicationServer.sendMessage(taskMessage);
                    this.tasksInProcess.put(clientId, task);
                }

            } else if (content instanceof TaskFinished) {
                if(LOGGER.isDebugEnabled()) LOGGER.debug("Got TaskFinished message from: " +clientId);
                this.tasksInProcess.remove(clientId);
                remainingTasks--;
                this.finished = remainingTasks == 0;
            }
        } catch (IOException e) {
            LOGGER.error("Error with message handling: " + e.getMessage());
        }
    }

    /**
     * Log the current status.
     */
    private void logStatus() {
        int finishedTasks = initialSizeOfTasks-remainingTasks;
        String status = this.finished ? "finished" : "current";
        String percentage = (double)finishedTasks / this.initialSizeOfTasks * 100 + "%";
        String minimum = this.getReadableMinimum();
        String currentPath = this.getReadableShortestPath();
        LOGGER.info("Calculation status: " + "\n" +
                "\t* remaining tasks: " + this.remainingTasks + "\n" +
                "\t* finished tasks: " + finishedTasks + "\n" +
                "\t* percentage: " + percentage + "\n" +
                "\t* calculation status: " + status + "\n" +
                "\t* current minimal distance: " + minimum + "\n" +
                "\t* current path: " + currentPath);
    }
}
