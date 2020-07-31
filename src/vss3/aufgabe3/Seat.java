package vss3.aufgabe3;

/**
 * A Seat on the table and one of the resources a Philosopher needs to eat.
 */
public class Seat {

    /**
     * Static increasing value for each new instance.
     */
    private static int counter = 0;
    /**
     * The ID of the instance.
     */
    private final int id = counter++;
    /**
     * Monitor Object for synchronization of fork uses.
     */
    public static final Object FORK_MONITOR = new Object();
    /**
     * The fork left to the seat.
     */
    private Fork leftFork;
    /**
     * The fork right to the seat.
     */
    private Fork rightFork;

    /**
     * Create an instance declaring left and right fork.
     *
     * @param leftFork  the left fork.
     * @param rightFork the right fork.
     */
    public Seat(final Fork leftFork, final Fork rightFork) {
        this.leftFork = leftFork;
        this.rightFork = rightFork;
    }

    /**
     * Get left and right fork.
     */
    public void getForks() {

        if (this.rightFork.getId() < this.leftFork.getId()) {
            this.rightFork.setUsed();
            this.leftFork.setUsed();
        } else {
            this.leftFork.setUsed();
            this.rightFork.setUsed();
        }

        System.out.println(Philosopher.currentPhilosopher() + " has forks.");
    }

    /**
     * Release left and right fork.
     */
    public void releaseForks() {

        this.rightFork.setUnused();
        this.leftFork.setUnused();

        System.out.println(Philosopher.currentPhilosopher() + " released forks.");
    }

    @Override
    public String toString() {
        return "seat " + this.id;
    }
}