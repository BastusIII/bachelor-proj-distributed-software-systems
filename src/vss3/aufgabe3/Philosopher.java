package vss3.aufgabe3;

import java.util.Random;

/**
 * Philosophers can eat on a table and think.
 * A philosopher is a thread.
 * All the philosophers have to share the same table and it's resources of seats and forks.
 * Philosopher sleep for a random time between 0 and the given maxOccupiedTime.
 */
public class Philosopher extends Thread {

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
    public Table table;
    /**
     * Random generator generates random eating/thinking time between 0 and maxOccupiedTime.
     */
    private static Random random = new Random();
    /**
     * Philosopher sleep for a random time between 0 and the given maxOccupiedTime.
     */
    private static int maxOccupiedTime = 100;
    /**
     * How often has the philosopher eaten.
     */
    private int timesEaten = 0;

    /**
     * Create an instance of Philosopher.
     *
     * @param table the table.
     */
    public Philosopher(final Table table, final boolean hungry) {
        this.table = table;
        this.hungry = hungry;
    }

    /**
     * Create an instance of Philosopher.
     *
     * @param table the table.
     */
    public Philosopher(final Table table) {
        this.table = table;
    }

    /**
     * Philosopher hungry?
     *
     * @return true or false
     */
    public boolean isHungry() {
        return hungry;
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
     * A philosopher wants to eat at the table and requests all the needed resources.
     * When he is ready he releases the resources.
     *
     * @throws InterruptedException
     */
    public void eat() throws InterruptedException {

        Seat seat = table.getSeat();
        seat.getForks();

        System.out.println(Philosopher.currentPhilosopher().toString() + " starts eating.");
        Thread.sleep(random.nextInt(maxOccupiedTime));
        System.out.println(Philosopher.currentPhilosopher().toString() + " stops eating for the " + ++timesEaten + "th time.");

        seat.releaseForks();
        table.releaseSeat(seat);
        System.out.println(Philosopher.currentPhilosopher().toString() + " released seat.");
    }

    /**
     * If a Philosopher wants to think. No one has something against that.
     *
     * @throws InterruptedException
     */
    public void think() throws InterruptedException {
        Thread.sleep(random.nextInt(maxOccupiedTime));
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
        try {
            while (true) {
                eat();
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

        if (id != that.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
