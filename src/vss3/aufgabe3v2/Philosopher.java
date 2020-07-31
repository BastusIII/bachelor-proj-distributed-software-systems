package vss3.aufgabe3v2;

import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Philosophers can eat on a table and think.
 * A philosopher is a thread.
 * All the philosophers have to share the same table and it's resources of seats and forks.
 * Philosopher sleep for a random time between 0 and the given maxOccupiedTime.
 * Eat : think time ratio of a balanced philosopher is 1:1.
 * Eat : think time ratio of a hungry philosopher is 1:1/25.
 */
public class Philosopher extends Thread {
	
	public static final int DID_NOT_GET_SEAT = -1;
    /**
     * The Logger.
     */
    public static final Logger LOGGER = Logger.getLogger(Philosopher.class);
    /**
     * Static increasing value for each new instance.
     */
    private static int counter = 0;
    /**
     * The ID of the instance.
     */
    private final int id = counter++;
    /**
     * Hungry philosophers may eat more often.
     */
    private boolean hungry = false;
    /**
     * Reference to the table instance.
     */
    private final Table table;
    /**
     * Random generator generates random eating/thinking time between 0 and maxOccupiedTime.
     */
    private static final Random random = new Random();
    /**
     * Philosopher sleep for a random time between 0 and the given maxOccupiedTime.
     */
    private final int maxOccupiedTime;
    /**
     * How often has the philosopher eaten.
     */
    private int timesEaten = 0;
    /**
     * Controller can set this field and philosopher reacts with Sleeping a while.
     */
    private AtomicBoolean tooGreedy = new AtomicBoolean(false);

    /**
     * Create an instance of Philosopher.
     *
     * @param table           the table.
     * @param hungry          set true, if the philosopher is hungry.
     * @param maxOccupiedTime set the maximum of time interval a philosopher eats and thinks.
     */
    public Philosopher(final Table table, final boolean hungry, final int maxOccupiedTime) {

        this.table = table;
        this.hungry = hungry;
        this.maxOccupiedTime = maxOccupiedTime;
    }

    /**
     * Create an instance of Philosopher.
     *
     * @param table           the table.
     * @param maxOccupiedTime set the maximum of time interval a philosopher eats and thinks.
     */
    public Philosopher(final Table table, final int maxOccupiedTime) {

        this.table = table;
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
     * A philosopher takes a seat and its forks and blocks these while eating.
     * When ready with eating, the philosopher gives back the forks an the seat.
     * Should the philosopher have no access to the seat or forks, he wants to give back, he just quits
     * without taking care of the consequences.
     *
     * @throws InterruptedException
     */
    public void eat() throws InterruptedException {
    	//TODO Seats nur einmal holen
        Seat[] seats = this.table.getSeats();
        int mySeat = DID_NOT_GET_SEAT;
        do {
            for (int i = 0; i < seats.length; ++i) {
                if (seats[i].take()) {
                    mySeat = i;
                    break;
                }
            }
            if (mySeat == DID_NOT_GET_SEAT) {
                //Wait until notified that the seat is free again.
                LOGGER.debug(Philosopher.currentPhilosopher().toString() + " waits for a while before starting to take seat again.");
                synchronized(seats[0]) {
                	if (seats[0].take()) {
                        mySeat = 0;
                	} else {
                		seats[0].wait();
                	}
                }
            }
        } while (mySeat == DID_NOT_GET_SEAT);

        try {
            seats[mySeat].takeForks();
        } catch (IllegalAccessException e) {
            return;
        }

        LOGGER.info(Philosopher.currentPhilosopher().toString() + " starts eating on " + seats[mySeat] + ".");
        Thread.sleep(random.nextInt(maxOccupiedTime));
        LOGGER.info(Philosopher.currentPhilosopher().toString() + " stops eating for the " + ++timesEaten + "th time on " + seats[mySeat] + ".");

        try {
            seats[mySeat].giveBackForks();
            seats[mySeat].giveBack();
        } catch (IllegalAccessException e) {
            return;
        }
    }

    /**
     * If a Philosopher wants to think. No one has something against that.
     * Hungry philosophers think 25 times less than balanced philosophers.
     *
     * @throws InterruptedException
     */
    public void think() throws InterruptedException {
        LOGGER.debug(Philosopher.currentPhilosopher().toString() + " starts thinking.");
        int timeToSleep = this.hungry ? random.nextInt(maxOccupiedTime) / 25 : random.nextInt(maxOccupiedTime);
        Thread.sleep(timeToSleep);
    }

    public void isTooGreedy() {
        this.tooGreedy.set(true);
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
        LOGGER.info(Philosopher.currentPhilosopher().toString() + " gets seats from table: " +
                Arrays.toString(this.table.getSeats()) + ".");
        try {
            while (true) {
                if (tooGreedy.get()) {
                    LOGGER.info(Philosopher.currentPhilosopher().toString() +
                            " has to sleep because of his greed");
                    Thread.sleep(this.maxOccupiedTime);
                    this.tooGreedy.set(false);
                } else {
                    eat();
                }
                think();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return (hungry ? "hungry " : "") + "philosopher " + this.id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Philosopher that = (Philosopher) o;

        return id == that.id;

    }

    @Override
    public int hashCode() {
        return id;
    }
}
