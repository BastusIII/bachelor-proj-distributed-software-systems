package vss3.aufgabe4;

import org.apache.log4j.Logger;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Checks repeatedly if the agents attached to a master are alive.
 */
public class LifeGuard extends Thread{

    /**
     * The logger for this class.
     */
	private static Logger LOGGER = Logger.getLogger(LifeGuard.class);

    /**
     * The time waited between each check.
     */
	private static final long MILLIS_DELAY = 1000 * 10;

    /**
     * The master whose agents are guarded.
     */
	private Master myMaster;

    /**
     * The registry used by the master.
     */
	private Registry registry;

    /**
     * Received a signal to stop.
     */
	private boolean stop = false;

    /**
     * A life guard to watch over the agents from given master.
     * @param master The master whose agents are guarded.
     * @throws RemoteException If network trouble and stuff occurs.
     */
	public LifeGuard(Master master) throws RemoteException {
		this.myMaster = master;
		registry = LocateRegistry.getRegistry(Master.REGISTRY_PORT);
	}

    /**
     * Check if the agents still answer and initiate revival if required.
     */
	private void checkAliveness() {
		LOGGER.info("Checking aliveness...");
		Collection<String> agentsToGuard = new LinkedList<>(myMaster.getAgents());

        /* Ping each agent and take further steps. */
		for(String agentName: agentsToGuard) {
			IAgent agent;
			try {
				agent = (IAgent) registry.lookup(agentName);
			} catch (RemoteException e) {
				LOGGER.error(e);
				continue;
			} catch (NotBoundException e) {
				LOGGER.error(e);
				continue;
			}
			try {
				agent.ping();
			} catch (RemoteException e) {
				// Could not ping.... agent must be dead!
				LOGGER.info("Dead agent found: " + agentName);
				try {
					myMaster.handleDeadAgent(agentName);
				} catch (DiningPhilosopherException de) {
					LOGGER.error("Could not handle dead agent: " + de.getMessage());
				}
			}
		}

		LOGGER.info("... all agents looking fine.");
	}

    /**
     * Periodically checks aliveness of agents.
     */
	public void run() {
		while(!this.isToStop()) {
			try {
				sleep(MILLIS_DELAY);
				checkAliveness();
			} catch (InterruptedException e) {
				LOGGER.error(e);
			}
		}
	}

    /**
     * Indicates if the termination of the life guard was requested.
     * @return True if the termination was requested.
     */
	public boolean isToStop() {
		return stop;
	}

    /**
     * Request termination of the life guard.
     */
	public void demandToStop() {
		this.stop = true;
	}
}
