package vss3.aufgabe4;

import org.apache.log4j.Logger;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * The Master is waiting for agents to connect, organizes these and offers a lifeguard checking if the ressources created on the agent are still available.
 */
public class Master implements IMaster{

	public static final int REGISTRY_PORT = 2000;
	public static final int CONTROLLER_SIZE = 99;

	private static final Logger LOGGER = Logger.getLogger(Master.class);

	private final Map<String, IAgent> agents = new HashMap<>();
	private final Collection<RemoteInformation> registeredRemotes = new LinkedList<>();
	private int numberOfPhilosophers = 0;
	private int numberOfSeats = 0;

	private Registry registry;
	private Controller controller;
	private LifeGuard lifeGuard;


	public Master() {
		try {
			new PhilosopherRegistry(REGISTRY_PORT, this);
		} catch (RemoteException e) {
			LOGGER.error(e);
		}
		try {
			registry = LocateRegistry.getRegistry(REGISTRY_PORT);
		} catch (RemoteException e) {
			LOGGER.error("Could not get registry.");
		}
        try {
            IMaster stub = (IMaster) UnicastRemoteObject.exportObject(this, 0);
            registry.bind("Master", stub);
        } catch (RemoteException | AlreadyBoundException e) {
            LOGGER.error(e);
        }

		try {
			controller = new Controller(CONTROLLER_SIZE, registry);
			IController stub = (IController) UnicastRemoteObject.exportObject(controller, 0);
			registry.bind("Controller", stub);
		} catch (RemoteException | AlreadyBoundException e) {
			LOGGER.error(e);
		}

		try {
			lifeGuard = new LifeGuard(this);
		} catch (RemoteException e) {
			LOGGER.error("WARNING: Master could not create LifeGuard!");
		}
		lifeGuard.start();

	}

    public <T extends Remote> void bindMe(T remote, String name){
        Remote stub = null;
        try {
            stub = UnicastRemoteObject.exportObject(remote, 0);
        } catch (RemoteException e) {
            LOGGER.error("Could not create stub for remote: " + name+e);
            return;
        }
        try {
            registry.bind(name, remote);
        } catch (RemoteException e) {
            LOGGER.error("Could not bind " +  name + " to registry: " + e);
        } catch (AlreadyBoundException e) {
            LOGGER.error("Could not bind " +  name + " to registry: " + e);
        }
    }

    public void unbindMe(String name){
        try {
            registry.unbind(name);
        } catch (RemoteException e) {
            LOGGER.error(e);
        } catch (NotBoundException e) {
            LOGGER.error(e);
        }
    }

	/**
	 * Registers an Agent, so that new Remotes can be assigned.
	 * @param name  The remote name of the agent.
	 * @param agent The Agents remote interface.
	 */
	public void register(final String name, final IAgent agent) {
		agents.put(name, agent);
		LOGGER.info("Registered new Agent with name: " + name);

		// push all seats and forks to the new registered agent
		try {
			Collection<String> remoteNames = getRemotes(ISeat.class);
			agent.setSeatList(remoteNames.toArray(new String[remoteNames.size()]));
			remoteNames = getRemotes(IFork.class);
			agent.setForkList(remoteNames.toArray(new String[remoteNames.size()]));
		} catch (RemoteException e) {
			LOGGER.error("Could not push seats and forks to newly registered agent: " + e);
		}
		// notify all threads, that are trying to restore a dead agent.
		synchronized(agents) {
			agents.notifyAll();
		}
	}

	/**
	 * Registers an Controller. Only display debug information.
	 * @param name  The remote name of the agent.
	 */
	public void register(final String name) {
		LOGGER.info("Registered new Controller with name: " + name);
	}

	public void addPhilosopher(boolean isHungry, int maxOccupiedTime) throws DiningPhilosopherException {
		int philosopherNumber = ++(this.numberOfPhilosophers);
		String agentName = getAgentNameWithLeastRemotes();
		String philosopherName = null;
		try {
			philosopherName = agents.get(agentName).createPhilosopher(philosopherNumber, isHungry, maxOccupiedTime);
		} catch (RemoteException e) {
			LOGGER.error(e);
		}
		PhilosopherInformation philosopherInfo = new PhilosopherInformation(philosopherName, agentName, philosopherNumber);
		philosopherInfo.setHungry(isHungry);
		philosopherInfo.setMaxOccupationTime(maxOccupiedTime);

		registeredRemotes.add(philosopherInfo);
		// tell the controller the new number of philosophers
		controller.setNumberOfPhilosophes(numberOfPhilosophers);
		LOGGER.info("Added " + philosopherName + " to " + agentName);
	}

	/**
	 * Removes the philosopher with the highest number from the system.
	 */
	public void removePhilosopher() {
		RemoteInformation philospherToRemove = getRemote(IPhilosopher.class, this.numberOfPhilosophers);
		registeredRemotes.remove(philospherToRemove);
		this.numberOfPhilosophers--;
		String agent = philospherToRemove.getAgentName();
		try {
			agents.get(agent).removePhilosopher(philospherToRemove.getNumber());
		} catch (RemoteException e) {
			LOGGER.error(e);
		}
		// tell the controller the new number of philosophers
		controller.setNumberOfPhilosophes(numberOfPhilosophers);
	}

	/**
	 * Adds a seat to the running configuration.
	 * @throws DiningPhilosopherException If no agents are available.
	 */
	public void addSeat() throws DiningPhilosopherException {
		int seatNumber = ++(this.numberOfSeats);
		String agentName = getAgentNameWithLeastRemotes();
		String seatName = null;
		try {
			seatName = agents.get(agentName).createSeat(seatNumber);
		} catch (RemoteException e) {
			LOGGER.error(e);
		}
		registeredRemotes.add(new RemoteInformation(ISeat.class, seatName, agentName, seatNumber));
		pushSeats();
		LOGGER.info("Added " + seatName + " to " + agentName);
		// if its the first seat we need two forks
		if(seatNumber == 1) {
			addFork(0);
		}
        if(seatNumber != 2) {
		    addFork(seatNumber);
        }
	}

	/**
	 * Adds a fork to the running configuration.
	 * @throws DiningPhilosopherException If no agents are available.
	 */
	private void addFork(int forkNumber) throws DiningPhilosopherException {
		String agentName = getAgentNameWithLeastRemotes();
		String forkName = null;
		try {
			forkName = agents.get(agentName).createFork(forkNumber);
		} catch (RemoteException e) {
			LOGGER.error(e);
		}
		registeredRemotes.add(new RemoteInformation(IFork.class, forkName, agentName, forkNumber));
		pushForks();
		LOGGER.info("Added " + forkName + " to " + agentName);
	}

	/**
	 * Removes the seat with the highest number from the running configuration.
	 */
	public void removeSeat() {
		RemoteInformation seatToRemove = getRemote(ISeat.class, this.numberOfSeats);
		registeredRemotes.remove(seatToRemove);
		this.numberOfSeats--;

		String agent = seatToRemove.getAgentName();
		try {
			agents.get(agent).removeSeat(seatToRemove.getNumber());
		} catch (RemoteException e) {
			LOGGER.error(e);
		}
		pushForks();
        if (seatToRemove.getNumber() != 2) {
		    removeFork(seatToRemove.getNumber());
        }

		if(seatToRemove.getNumber() == 1) {
			removeFork(0);
		}

	}

	/**
	 * Removes the fork with given number from running configuration.
	 * @param forkNumber The number of the fork to remove.
	 */
	public void removeFork(int forkNumber) {
		RemoteInformation forkToRemove = getRemote(IFork.class, forkNumber);
		String agent = forkToRemove.getAgentName();
		try {
			agents.get(agent).removeFork(forkNumber);
		} catch (RemoteException e) {
			LOGGER.error(e);
		}
		registeredRemotes.remove(forkToRemove);
		pushForks();
	}

	/**
	 * Returns the name of the agent with least attached remotes.
	 * @return The name of the agent as used in the Registry.
	 * @throws DiningPhilosopherException If no agents are available.
	 */
	public String getAgentNameWithLeastRemotes() throws DiningPhilosopherException{
		if(agents.size() < 1){
			throw new DiningPhilosopherException("Could not get agent with least remotes: no agents registered.");
		}
		int minRemotes = Integer.MAX_VALUE;
		String agentWithLeastRemotes = null;
		for(String agentName: agents.keySet()) {
			int remotesOnThisAgent = getRemotes(agentName).size();
			if(remotesOnThisAgent < minRemotes) {
				minRemotes = remotesOnThisAgent;
				agentWithLeastRemotes = agentName;
			}
		}
		return agentWithLeastRemotes;
	}

	/**
	 * Returns the name of the agent to whom the given remote is attached.
	 * @param remoteName The name of the remote to search the agent for.
	 * @return The name of the agent.
	 */
	public String findAgentWithRemote(final String remoteName) {
		for(RemoteInformation information: registeredRemotes) {
			if(information.getName().equals(remoteName)) {
				return information.getAgentName();
			}
		}
		return null;
	}

	/**
	 * Push the seat list to all registered agents.
	 */
	private void pushSeats() {
		Collection<String> remoteNames = getRemotes(ISeat.class);
		String[] seatNames = remoteNames.toArray(new String[remoteNames.size()]);
		for(IAgent agent: agents.values()) {
			try {
				agent.setSeatList(seatNames);
			} catch (RemoteException e) {
				LOGGER.error(e);
			}
		}
	}

	/**
	 * Push the fork list to all registered agents.
	 */
	private void pushForks() {
		Collection<String> remoteNames = getRemotes(IFork.class);
		String[] forkNames = remoteNames.toArray(new String[remoteNames.size()]);
		for(IAgent agent: agents.values()) {
			try {
				agent.setForkList(forkNames);
			} catch (RemoteException e) {
				LOGGER.error(e);
			}
		}
	}

	/**
	 * Returns a collection with all the names of registered remotes of the given class.
	 * @param clazz The class of the remotes.
	 * @return The names of all registered remotes.
	 */
	public Collection<String> getRemotes(Class<? extends Remote> clazz) {
		Collection<String> remoteNames = new LinkedList<>();

		for(RemoteInformation remoteInformation: this.registeredRemotes) {
			if(remoteInformation.getRemoteClass() == clazz) {
				remoteNames.add(remoteInformation.getName());
			}
		}

		return remoteNames;
	}

	/**
	 * Returns a collection with all the names of registered remotes running on the agent.
	 * @param agent The agent where the remotes are running.
	 * @return The names of all registered remotes.
	 */
	public Collection<String> getRemotes(final String agent) {
		Collection<String> remoteNames = new LinkedList<>();

		for(RemoteInformation remoteInformation: this.registeredRemotes) {
			if(remoteInformation.getAgentName().equals(agent)) {
				remoteNames.add(remoteInformation.getName());
			}
		}

		return remoteNames;
	}

	/**
	 * Returns the information of the remote of given class and with given number.
	 * @param clazz The class of the remote.
	 * @param number The number of the remote.
	 * @return The remote information.
	 */
	public RemoteInformation getRemote(final Class<? extends Remote> clazz, final int number) {
		for(RemoteInformation information: registeredRemotes) {
			if(information.getNumber() == (number) && information.getRemoteClass().equals(clazz)) {
				return information;
			}
		}
		return null;
	}

	/**
	 * Returns a collection of all registered agents names.
	 * @return The registered agents names.
	 */
	public Collection<String> getAgents() {
		return agents.keySet();
	}

	/**
	 * Handles a dead agent and tries to restore the remotes on other agents.
	 * @param agent The agent whose death is to handle.
	 */
	public void handleDeadAgent(final String agent) throws DiningPhilosopherException {
		unbindDeadAgent(agent);
		agents.remove(agent);

		// wait while not agents are available.
		while(agents.size() < 1) {
			synchronized (agents) {
				try {
					LOGGER.info("Trying to handle dead agent, but no living agent remaining. Waiting for new agent...");
					agents.wait();
				} catch (InterruptedException e) {
					LOGGER.error(e);
				}
			}
		}

		for(RemoteInformation remoteInfo : registeredRemotes) {
			if(remoteInfo.getAgentName().equals(agent)) {
				// only revive remotes from given agent
				LOGGER.info("Reviving: " + remoteInfo.getName());
				reviveRemote(remoteInfo);
			}
		}

		pushSeats();
		pushForks();
	}

	/**
	 * Unbinds an agent and all his dead remotes from the registry.
	 * @param agent The agent to unbind.
	 */
	private void unbindDeadAgent(final String agent) {
		try {
			registry.unbind(agent);

			for(String remoteName : getRemotes(agent)) {
				registry.unbind(remoteName);
				LOGGER.info("Unbound: " + remoteName);
			}
		} catch (RemoteException | NotBoundException e) {
			LOGGER.error("Could not unbind a remote, while handling dead agent: " + e.getMessage());
		}
	}

	/**
	 * Tries to revive a dead remote.
	 * @param remote The dead remote.
	 * @throws DiningPhilosopherException If the remote could not be revived.
	 */
	private void reviveRemote(final RemoteInformation remote) throws DiningPhilosopherException {
		String agentName;
		try {
			 agentName = getAgentNameWithLeastRemotes();
		 } catch (DiningPhilosopherException de) {
			 throw new DiningPhilosopherException("Could not revive " + remote.getName() + ": " + de.getMessage());
		 }

		String newRemoteName = reviveRemoteOnAgent(remote, agents.get(agentName));
		String oldRemoteName = remote.getName();
		remote.setName(newRemoteName);
		remote.setAgentName(agentName);
		LOGGER.info("Revived " + oldRemoteName + " as " + newRemoteName + " on " + agentName +".");
	}

	/**
	 * Tries to revive the remote for given remote information on an agent.
	 * @param remoteInfo The information of the remote that should be revived.
	 * @param agent The agent on whom the remote is to be revived.
	 * @return The new name of the successfully revived remote.
	 * @throws DiningPhilosopherException If the remote could not be revived.
	 */
	private String reviveRemoteOnAgent(final RemoteInformation remoteInfo, final IAgent agent) throws DiningPhilosopherException {
		// revive philosopher
		if(remoteInfo.getRemoteClass().equals(IPhilosopher.class)) {
			PhilosopherInformation philosopherInfo;
			try {
				philosopherInfo = (PhilosopherInformation) remoteInfo;
			} catch (ClassCastException cce) {
				throw new DiningPhilosopherException("Philosopher Remoteinformation contained no philospher specific data.");
			}
			try {
				return agent.createPhilosopher(philosopherInfo.getNumber(), philosopherInfo.isHungry(), philosopherInfo.getMaxOccupationTime());
			} catch (RemoteException e) {
				throw new DiningPhilosopherException("Could not revive " + philosopherInfo.getName() + ": " + e.getMessage());
			}
		}
		// revive seat
		if(remoteInfo.getRemoteClass().equals(ISeat.class)) {
			try {
				return agent.createSeat(remoteInfo.getNumber());
			} catch (RemoteException e) {
				throw new DiningPhilosopherException("Could not revive " + remoteInfo.getName() + ": " + e.getMessage());
			}
		}

		// revive fork
		if(remoteInfo.getRemoteClass().equals(IFork.class)) {
			try {
				return agent.createFork(remoteInfo.getNumber());
			} catch (RemoteException e) {
				throw new DiningPhilosopherException("Could not revive " + remoteInfo.getName() + ": " + e.getMessage());
			}
		}

		throw new DiningPhilosopherException("Could not revive " + remoteInfo.getName() + ": Unknown class.");
	}
}
