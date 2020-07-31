package vss3.aufgabe3v2;

import org.apache.log4j.Logger;

/**
 * A Seat on the table and one of the resources a Philosopher needs to eat.
 */
public class Seat {

    /**
     * The Logger.
     */
    public static final Logger LOGGER = Logger.getLogger(Seat.class);
    /**
     * Static increasing value for each new instance.
     */
    private static int counter = 0;
    /**
     * The ID of the instance.
     */
    private final int id = counter++;
    /**
     * The table this seat is placed at.
     */
    private final Table table;
    /**
     * Indicates if the seat is used.
     */
    private final Fork leftFork;
    /**
     * The fork right to the seat.
     */
    private final Fork rightFork;
    /**
     * The philosopher, that has taken the seat.
     */
    private Philosopher owner = null;

    /**
     * Create an instance declaring left and right fork.
     *
     * @param leftFork  the left fork.
     * @param rightFork the right fork.
     */
    public Seat(final Table table, final Fork leftFork, final Fork rightFork) {
        this.leftFork = leftFork;
        this.rightFork = rightFork;
        this.table = table;
    }

    synchronized public boolean take() {

        if (this.owner != null) {
            LOGGER.debug(Philosopher.currentPhilosopher() + " tried to take seat " +
                    this.id + ".");
            return false;
        }
        this.owner = Philosopher.currentPhilosopher();
        this.table.getController().philosopherTookSeat();
        LOGGER.debug(Philosopher.currentPhilosopher().toString() + " took " + this + ".");
        return true;
    }

    synchronized public void giveBack() throws IllegalAccessException {
        if (Philosopher.currentPhilosopher() == this.owner) {
            this.owner = null;
            LOGGER.debug(Philosopher.currentPhilosopher().toString() + " gave back back " + this + ".");
        } else {
            LOGGER.error(Philosopher.currentPhilosopher() + " tried to give back " +
                    this + " illegally.");
            throw new IllegalAccessException();
        }
        this.notify();
    }

    /**
     * Get left and right fork.
     */
    public void takeForks() throws IllegalAccessException {

        if (Philosopher.currentPhilosopher() == this.owner) {
            if (this.rightFork.getId() < this.leftFork.getId()) {
                this.rightFork.take();
                this.leftFork.take();
            } else {
                this.leftFork.take();
                this.rightFork.take();
            }

            LOGGER.debug(Philosopher.currentPhilosopher() + " took forks.");
        } else {
            LOGGER.error(Philosopher.currentPhilosopher() + " tried to take forks of " +
                    this + " illegally.");
            throw new IllegalAccessException();
        }
    }

    /**
     * Release left and right fork.
     */
    public void giveBackForks() throws IllegalAccessException {

        if (Philosopher.currentPhilosopher() == this.owner) {
            this.rightFork.giveBack();
            this.leftFork.giveBack();
            LOGGER.debug(Philosopher.currentPhilosopher() + " gave back forks.");
        } else {
            LOGGER.error(Philosopher.currentPhilosopher() + " tried to give back forks of " +
                    this + " illegally.");
            throw new IllegalAccessException();
        }

    }

    @Override
    public String toString() {
        return "seat " + this.id;
    }
}