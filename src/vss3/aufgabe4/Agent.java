package vss3.aufgabe4;

import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Agent
 * 
 * The agent registered by the server and offers functions to create/remove Philosopher, seats, forks.
 * 
 * @author Waldleitner
 *
 */
public class Agent implements IAgent {

	/**
	 * Controller for eat counting
	 */
	private IController controller = null;
	/**
	 * Master on the server
	 */
	private IMaster master = null;
	/**
	 * Registry on the server
	 */
	private Registry server = null;

	/**
	 * List for all local Philosopher
	 */
	private Map<String, IPhilosopher> myPhilosopher = new HashMap<String, IPhilosopher>();
	/**
	 * List for all local forks.
	 */
	private Map<String, IFork> myForks = new HashMap<String, IFork>();
	/**
	 * List for all local seats.
	 */
	private Map<String, ISeat> mySeats = new HashMap<String, ISeat>();

	/**
	 * List of all registered forks.
	 */
	private String[] forkList;
	/**
	 * List of all registered seats.
	 */
	private String[] seatList;
	/**
	 * Logger
	 */
	private Logger LOGGER = Logger.getLogger(Agent.class);

	/**
	 * Constructor
	 * @param ip	IP-address from the server
	 * @param port	Port from the server
	 */
	public Agent(String ip, int port) {
		while (server == null) {
			try {
				server = LocateRegistry.getRegistry(ip, port);
			} catch (RemoteException e) {
				LOGGER.error("Host (Master) not found", e);
			}
		}
		while (master == null) {
			try {
				master = (IMaster) server.lookup("Master");
			} catch (AccessException e) {
				LOGGER.error(e);
			} catch (RemoteException e) {
				LOGGER.error(e);
			} catch (NotBoundException e) {
				LOGGER.error("Controller not found in registry.", e);
			}
		}
		LOGGER.info("Master found");
		while (controller == null) {
			try {
				controller = (IController) server.lookup("Controller");
			} catch (AccessException e) {
				LOGGER.error(e);
			} catch (RemoteException e) {
				LOGGER.error(e);
			} catch (NotBoundException e) {
				LOGGER.error("Controller not found in registry.", e);
			}

		}
		LOGGER.info("Connected to Server");

		int i = 1;
		while (i < Integer.MAX_VALUE) {
			try {
				server.lookup("Agent" + i);
			} catch (AccessException e) {
				LOGGER.error(e);
			} catch (RemoteException e) {
				LOGGER.error(e);
			} catch (NotBoundException e) {
				break;
			}
			i++;
		}
		boolean bindingSuccessful = false;
		IAgent me = null;
		while (me == null) {
			try {
				me = (IAgent) UnicastRemoteObject.exportObject(this, 0);
			} catch (RemoteException e) {
				LOGGER.error(e);
			}
		}
		try {
			LOGGER.info("Trying to bind me on server.");
			master.bindMe(me, "Agent" + i);
		} catch (RemoteException e) {
			LOGGER.error(e);
		}
		bindingSuccessful = true;
		LOGGER.info("Binding was successful!");
	}

	@Override
	public void setSeatList(String[] seatList) {
		this.seatList = seatList;
		LOGGER.debug("Getting new seatList with " + seatList.length + " seats.");
		if (seatList.length > 0) {
			synchronized (this) {
				this.notifyAll();
			}
		}
	}

	@Override
	public void setForkList(String[] forkList) {
		this.forkList = forkList;
		LOGGER.debug("Getting new forkList with " + forkList.length + " forks.");
		if (forkList.length > 1) {
			synchronized (this) {
				this.notifyAll();
			}
		}
	}

	@Override
	public boolean ping() {
		return true;
	}

	@Override
	public IController getController() {
		return controller;
	}

	/**
	 * get the list of seats
	 * 
	 * @return Array of seats
	 */
	public String[] getSeatList() {
		return seatList;
	}

	/**
	 * get the list of forks
	 * 
	 * @return Array of forks
	 */
	public String[] getForkList() {
		return forkList;
	}

	@Override
	public String createPhilosopher(int id, boolean hungry, int maxOccupiedTime) {
		Philosopher phil = new Philosopher(id, this, hungry, maxOccupiedTime);
		myPhilosopher.put(phil.getPhilosopherName(), (IPhilosopher) phil);

		boolean isSuccessfulBinded = false;
		while (!isSuccessfulBinded) {
			try {
				master.bindMe((IPhilosopher) phil, phil.getPhilosopherName());
				isSuccessfulBinded = true;
			} catch (AccessException e) {
				LOGGER.error(e);
			} catch (RemoteException e) {
				LOGGER.error(e);
			}
		}
		Thread th = (Philosopher) phil;
		th.start();
		return phil.getPhilosopherName();
	}

	@Override
	public void removePhilosopher(int id) {
		Philosopher phil = (Philosopher) myPhilosopher.get(Philosopher
				.getNameForPhilosopher(id));
		if (phil == null) {
			LOGGER.info("Cant find Philosopher" + id + ".");
			return;
		}
		phil.stopPhilosoph();
		boolean isSuccessfulUnbinded = false;
		// wait until philosopher is ready to be deleted
		phil.blockUntilRemovable();
		while (!isSuccessfulUnbinded) {
			try {
				master.unbindMe(phil.getPhilosopherName());
				isSuccessfulUnbinded = true;
			} catch (AccessException e) {
				LOGGER.error(e);
			} catch (RemoteException e) {
				LOGGER.error(e);
			}
		}
		myPhilosopher.remove(Philosopher.getNameForPhilosopher(id));
		LOGGER.info("Philosoph" + id + " succsessfull removed.");
	}

	@Override
	public String createFork(int id) {
		Fork fork = new Fork(id, this);
		myForks.put(fork.getForkName(), (IFork) fork);
		boolean isSuccessfulBinded = false;
		while (!isSuccessfulBinded) {
			try {
				master.bindMe((IFork) fork, fork.getForkName());
				isSuccessfulBinded = true;
			} catch (AccessException e) {
				LOGGER.error(e);
			} catch (RemoteException e) {
				LOGGER.error(e);
			}
		}
		LOGGER.info("Created fork with id:" + id);
		return fork.getForkName();
	}

	@Override
	public void removeFork(int id) {
		IFork fork = myForks.get(Fork.getNameForFork(id));
		if (fork == null) {
			LOGGER.info("Cant find Fork" + id + ".");
			return;
		}
		ISeat seat = mySeats.get(Seat.getNameForSeat(id));
		if (seat!=null) {
			boolean isTaken = false;
			while (!isTaken) {
				try {
					isTaken = seat.take("Agent");
				} catch (RemoteException e1) {
					LOGGER.error(e1);
				}
				if (!isTaken) {
					try {
						seat.waitForEmptySeat();
					} catch (RemoteException e) {
						LOGGER.error(e);
					}
				}
			}
		}
		boolean isSuccessfulUnbinded = false;
		while (!isSuccessfulUnbinded) {
			try {
				master.unbindMe(fork.getForkName());
				isSuccessfulUnbinded = true;
			} catch (AccessException e) {
				LOGGER.error(e);
			} catch (RemoteException e) {
				LOGGER.error(e);
			}
		}
		myForks.remove(Fork.getNameForFork(id));
		if (seat!=null) {
			try {
				seat.giveBack("Agent");
			} catch (RemoteException e) {
				LOGGER.error(e);
			}
		}
		LOGGER.info("Fork" + id + " successfull removed.");
	}

	@Override
	public String createSeat(int id) {
		Seat seat = new Seat(id, this);
		mySeats.put(seat.getSeatName(), (ISeat) seat);

		boolean isSuccessfulBinded = false;
		while (!isSuccessfulBinded) {
			try {
				master.bindMe((ISeat) seat, seat.getSeatName());
				isSuccessfulBinded = true;
			} catch (AccessException e) {
				LOGGER.error(e);
			} catch (RemoteException e) {
				LOGGER.error(e);
			}
		}
		LOGGER.info("Created new seat with id: " + id);
		return seat.getSeatName();
	}

	@Override
	public void removeSeat(int id) {
		ISeat seat = mySeats.get(Seat.getNameForSeat(id));
		if (seat == null) {
			LOGGER.info("Cant find Seat" + id + ".");
			return;
		}
		boolean isTaken = false;
		while (!isTaken) {
			try {
				isTaken = seat.take("Agent");
			} catch (RemoteException e1) {
				LOGGER.error(e1);
			}
			if (!isTaken) {
				try {
					seat.waitForEmptySeat();
				} catch (RemoteException e) {
					LOGGER.error(e);
				}
			}
		}
		boolean isSuccessfulUnbinded = false;
		while (!isSuccessfulUnbinded) {
			try {
				master.unbindMe(seat.getSeatName());
				isSuccessfulUnbinded = true;
			} catch (AccessException e) {
				LOGGER.error(e);
			} catch (RemoteException e) {
				LOGGER.error(e);
			}
		}
		mySeats.remove(Seat.getNameForSeat(id));
		LOGGER.info("Seat" + id + " successfull removed");
	}

	/**
	 * Get local Fork or RemoteFork
	 * 
	 * @param name
	 *            name of the Fork
	 * @return local Fork or Remote Fork
	 */
	public IFork getFork(String name) {
		IFork fork = myForks.get(name);
		if (fork == null) {
			try {
				fork = (IFork) server.lookup(name);
				myForks.put(fork.getForkName(), fork);
			} catch (AccessException e) {
				LOGGER.error(e);
			} catch (RemoteException e) {
				LOGGER.error(e);
			} catch (NotBoundException e) {
				LOGGER.error(e);
			}
		}
		return fork;
	}

	/**
	 * Get local Seat or Remote Seat
	 * 
	 * @param name
	 *            name of the Seat
	 * @return local Seat or Remote Seat
	 */
	public ISeat getSeat(String name) {
		ISeat seat = mySeats.get(name);

		if (seat == null) {
			try {
				seat = (ISeat) server.lookup(name);
				mySeats.put(seat.getSeatName(), seat);
			} catch (AccessException e) {
				LOGGER.error(e);
			} catch (RemoteException e) {
				LOGGER.error(e);
			} catch (NotBoundException e) {
				LOGGER.error(e);
			}
		}
		return seat;
	}
}
