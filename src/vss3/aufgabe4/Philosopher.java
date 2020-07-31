package vss3.aufgabe4;

import org.apache.log4j.Logger;

import sun.util.logging.resources.logging;

import java.io.Serializable;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Philosophers can eat on a table and think. A philosopher is a thread. All the
 * philosophers have to share the same table and it's resources of seats and
 * forks. Philosopher sleep for a random time between 0 and the given
 * maxOccupiedTime. Eat : think time ratio of a balanced philosopher is 1:1. Eat
 * : think time ratio of a hungry philosopher is 1:1/25.
 */
public class Philosopher extends Thread implements IPhilosopher, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int DID_NOT_GET_SEAT = -1;
	/**
	 * The Logger.
	 */
	public static final Logger LOGGER = Logger.getLogger(Philosopher.class);
	/**
	 * The ID of the instance.
	 */
	private int id;
	/**
	 * Name of the Philosopher in the registry
	 */
	private final String name;
	/**
	 * Hungry philosophers may eat more often.
	 */
	private boolean hungry = false;
	/**
	 * Reference to the Agent instance.
	 */
	private IAgent agent;
	/**
	 * Random generator generates random eating/thinking time between 0 and
	 * maxOccupiedTime.
	 */
	private static final Random random = new Random();
	/**
	 * Philosopher sleep for a random time between 0 and the given
	 * maxOccupiedTime.
	 */
	private final int maxOccupiedTime;
	/**
	 * How often has the philosopher eaten.
	 */
	private int timesEaten = 0;
	/**
	 * Controller can set this field and philosopher reacts with Sleeping a
	 * while.
	 */
	private boolean tooGreedy =false;

    /**
     * Indicates whether the philosoph is stopped and ready to be deleted.
     */
    private AtomicBoolean deletable = new AtomicBoolean(false);

	/**
	 * seatList
	 */
	private String[] seats = null;
	/**
	 * forkList
	 */
	private String[] forks = null;
	/**
	 * Is Philosopher interrupted?
	 */
	private boolean isInterrupted = false;

	/**
	 * Name of a Philosopher.
	 * @param id	Number of the Philosopher.
	 * @return		Name of the Philosopher.
	 */
	public static String getNameForPhilosopher(int id) {
		return "Philosoph" + id;
	}

	/**
	 * Create an instance of Philosopher.
	 *
	 * @param hungry
	 *            set true, if the philosopher is hungry.
	 * @param maxOccupiedTime
	 *            set the maximum of time interval a philosopher eats and
	 *            thinks.
	 */
	public Philosopher(final int id, final Agent agent, final boolean hungry,
			final int maxOccupiedTime) {

		this.agent = agent;
		this.id = id;
		this.name = getNameForPhilosopher(id);
		this.hungry = hungry;
		this.maxOccupiedTime = maxOccupiedTime;
	}

	/**
	 * Philosopher's ID?
	 * 
	 * @return the ID.
	 */
	public int getPhilosopherId() {
		return id;
	}

	/**
	 * Philosopher's name?
	 * 
	 * @return the name.
	 */
	public String getPhilosopherName() {
		return name;
	}

	/**
	 * A philosopher takes a seat and its forks and blocks these while eating.
	 * When ready with eating, the philosopher gives back the forks an the seat.
	 * Should the philosopher have no access to the seat or forks, he wants to
	 * give back, he just quits without taking care of the consequences.
	 * 
	 * @throws InterruptedException
	 */
	private void eat() {
		boolean hasSuccessfulEaten = false;
		while (!hasSuccessfulEaten) {
			int mySeat = DID_NOT_GET_SEAT;
			int leftForkId;
			int rightForkId;
			IFork leftFork = null;
			IFork rightFork = null;
			ISeat seat = null;
			do {
				getLists();
				if(isInterrupted){
					return;
				}
				
				// Searching seat to take
				for (int i = 0; i < seats.length; i++) {
					try {
						seat = agent.getSeat(seats[(i + id - 1) % seats.length]);
					} catch (RemoteException e) {
						LOGGER.error(e);
					}
					if (seat == null) {
						continue;
					}
					if (takeSeat(seat)) {
						mySeat = (i + id - 1) % seats.length;
						break;
					}
				}
				if (mySeat == DID_NOT_GET_SEAT) {
					// Wait until notified that the seat is free again.
					LOGGER.debug(Philosopher.currentPhilosopher().toString()
							+ " waits for a while before starting to take seat again.");
					try {
						seat = agent.getSeat(seats[(id - 1) % seats.length]);
					} catch (RemoteException e1) {
						LOGGER.error(e1);
					}
                    if (seat == null) {
                        continue;
                    }
					if (takeSeat(seat)) {
						mySeat = id - 1 % seats.length;
					} else {
						try {
							seat.waitForEmptySeat();
						} catch (RemoteException e) {
							LOGGER.error(e);
						}
					}
				}
			} while (mySeat == DID_NOT_GET_SEAT);

			
			//Trying to take forks
			leftForkId = mySeat;
			rightForkId = (mySeat + 1) % seats.length;
			if(seats.length==1){
				rightForkId = 1;
			}

			try {
				leftFork = agent.getFork(forks[leftForkId]);
				rightFork = agent.getFork(forks[rightForkId]);
			} catch (RemoteException e2) {
				LOGGER.error(e2);
			}

			// Handle some errors
			if (leftFork == null || rightFork == null) {
				giveBackSeat(seat);
				LOGGER.info("Fork(s) could not be reached. Starting new eating.");
				continue;
			}
			if (rightForkId < leftForkId) {
				if (!takeFork(rightFork)) {
					giveBackSeat(seat);
					LOGGER.info("Fork(s) could not be reached. Starting new eating.");
					continue;
				}
				if (!takeFork(leftFork)) {
					giveBackSeat(seat);
					giveBackFork(rightFork);
					LOGGER.info("Fork(s) could not be reached. Starting new eating.");
					continue;
				}
			} else {
				if (!takeFork(leftFork)) {
					giveBackSeat(seat);
					LOGGER.info("Fork(s) could not be reached. Starting new eating.");
					continue;
				}
				if (!takeFork(rightFork)) {
					giveBackSeat(seat);
					giveBackFork(leftFork);
					LOGGER.info("Fork(s) could not be reached. Starting new eating.");
					continue;
				}
			}
			LOGGER.debug(currentPhilosopher() + " took forks.");

			LOGGER.info(currentPhilosopher().toString() + " starts eating on "
					+ seats[mySeat] + ".");
			
			// Says the controller will eat now.
			try {
				this.agent.getController().philosopherStartsEating(id, name);
			} catch (RemoteException e) {
				LOGGER.error(e);
			}

			// Eats
			try {
				Thread.sleep(random.nextInt(maxOccupiedTime));
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			LOGGER.info(currentPhilosopher().toString()
					+ " stops eating for the " + ++timesEaten + "th time on "
					+ seats[mySeat] + ".");

			// Give back seat and forks.
			giveBackFork(leftFork);
			giveBackFork(rightFork);
			giveBackSeat(seat);
			hasSuccessfulEaten = true;
		}
	}

	/**
	 * Get seat and fork list from the agent and blocks until the lists are available.
	 */
	private void getLists() {
		while (!isInterrupted) {
			try {
				seats = this.agent.getSeatList();
				forks = this.agent.getForkList();
			} catch (RemoteException e1) {
				LOGGER.error(e1);
			}
			if (seats == null || seats.length <= 0 || forks == null
					|| forks.length <= 1) {
				try {
					synchronized (agent) {
						agent.wait();
					}
				} catch (InterruptedException e) {
					LOGGER.error(e);
				}
				LOGGER.debug(name + " is notified.");
			} else {
				break;
			}

		}
		LOGGER.debug(name + " has seat-/fork-list.");
	}

	/**
	 * Take a seat.
	 * @param seat	The seat to take.
	 * 
	 * @return	true if seat could be taken.
	 * 			false is seat is not reachable.
	 */
	private boolean takeSeat(ISeat seat) {
		boolean result = false;
		try {
			result = seat.take(name);
		} catch (RemoteException e) {
			LOGGER.error(e);
		}
		return result;
	}

	/**
	 * Give back a seat.
	 * 
	 * @param seat	The seat to give back.
	 */
	private void giveBackSeat(ISeat seat) {
		boolean isSuccessfulGiven = false;
		while (!isSuccessfulGiven) {
			try {
				seat.giveBack(name);
				isSuccessfulGiven = true;
			} catch (RemoteException e) {
				LOGGER.error(e);
				break;
			}
		}
	}

	/**
	 * Take a fork.
	 * 
	 * @param fork	The fork to take.
	 * 
	 * @return		true if fork could be taken.
	 * 				false if fork is not reachable.
	 */
	private boolean takeFork(IFork fork) {
		try {
			fork.take(name);
		} catch (RemoteException e) {
			LOGGER.error(e);
			return false;
		}
		return true;
	}

	/**
	 * Give back a fork.
	 * 
	 * @param fork	The fork to give back.
	 */
	private void giveBackFork(IFork fork) {
		boolean isSuccessfulGiven = false;
		while (!isSuccessfulGiven) {
			try {
				fork.giveBack(name);
				isSuccessfulGiven = true;
			} catch (RemoteException e) {
				LOGGER.error(e);
			}
		}
	}

	/**
	 * If a Philosopher wants to think. No one has something against that.
	 * Hungry philosophers think 25 times less than balanced philosophers.
	 * 
	 * @throws InterruptedException
	 */
	private void think() throws InterruptedException {
		LOGGER.debug(Philosopher.currentPhilosopher().toString()
				+ " starts thinking.");
		int timeToSleep = this.hungry ? random.nextInt(maxOccupiedTime) / 25
				: random.nextInt(maxOccupiedTime);
		Thread.sleep(timeToSleep);
	}

	@Override
	public void isTooGreedy() {
		tooGreedy=true;
        System.out.println("Set "+tooGreedy);
	}

	/**
	 * Returns the Philosopher of the current Thread.
	 * 
	 * @return the philosopher that is currently working.
	 */
	public static Philosopher currentPhilosopher() {
		return (Philosopher) Thread.currentThread();
	}

	@Override
	public void run() {
		LOGGER.info(Philosopher.currentPhilosopher().toString() + " is born.");
		LOGGER.info(Philosopher.currentPhilosopher().toString()
				+ " is searching for a seat.");
		try {
			while (!isInterrupted) {
                System.out.println(tooGreedy);
				if (tooGreedy) {
					LOGGER.info(Philosopher.currentPhilosopher().toString()
							+ " has to sleep because of his greed");
					Thread.sleep(this.maxOccupiedTime*4);
					this.tooGreedy=false;
				} else {
					eat();
				}
				think();
			}
            synchronized (this.deletable) {
                this.deletable.set(true);
                this.deletable.notifyAll();
            }

		} catch (InterruptedException e) {
			LOGGER.error(e);
		}
	}

	/**
	 * Stop the Philosoph.
	 */
	public void stopPhilosoph() {
		this.isInterrupted = true;
		synchronized(agent) {
			agent.notifyAll();
		}
	}

	@Override
	public String toString() {
		return (hungry ? "hungry " : "") + "philosopher " + this.id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Philosopher that = (Philosopher) o;

		return id == that.id;

	}

	@Override
	public int hashCode() {
		return id;
	}

	/**
	 * Blocks until the Philosopher can be removed
	 */
    public void blockUntilRemovable() {
        synchronized (this.deletable) {
            while(!this.deletable.get()) {
                try {
                    this.deletable.wait();
                } catch (InterruptedException e) {
                    LOGGER.error("Waiting for deletable was interrupted.");
                }
            }
        }
        return;
    }
}
