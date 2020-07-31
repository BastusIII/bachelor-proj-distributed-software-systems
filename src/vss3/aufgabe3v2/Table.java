package vss3.aufgabe3v2;

import org.apache.log4j.Logger;

/**
 * The class Table implements a Table the Philosophers can eat at.
 * The table also controls the feature, that hungry Controllers may eat more often.
 */
public class Table {

    /**
     * The Logger.
     */
    public static final Logger LOGGER = Logger.getLogger(Table.class);
    /**
     * Controller for table.
     */
    private final Controller controller;
    /**
     * List of free seats.
     */
    private final Seat[] seats;

    /**
     * Create an instance of table that initialises all the seats and forks.
     *
     * @param numberOfSeats the number of seats at the table.
     * @param controller    the controller instance.
     */
    public Table(final Controller controller, final int numberOfSeats) {

        this.controller = controller;
        Fork[] forks = new Fork[numberOfSeats];
        this.seats = new Seat[numberOfSeats];
        for (int i = 0; i < numberOfSeats; ++i) {
            forks[i] = new Fork();
        }
        for (int i = 0; i < numberOfSeats - 1; ++i) {
            this.seats[i] = new Seat(this, forks[i], forks[i + 1]);
        }
        seats[numberOfSeats - 1] = new Seat(this, forks[numberOfSeats - 1], forks[0]);
        LOGGER.info("Table created with " + numberOfSeats + " seats.");
    }

    /**
     * Get the seats array.
     * The returned array differs from philosopher to philosopher, depending on his id.
     * For example id = 3, number of seats = 10.
     * The returned seats array is: [3,4,5,6,7,8,9,0,1,2]
     * id = 13, number of seats = 7 -> [6,0,1,2,3,4,5]
     * id = 0, number of seats = 8 -> [0,1,2,3,4,5,6,7]
     * id = 8, number of seats = 8 -> [0,1,2,3,4,5,6,7]
     *
     * @return the seats array.
     */
    public Seat[] getSeats() {

        Seat[] copy = new Seat[seats.length];
        int id = Philosopher.currentPhilosopher().getPhilosopherId();
        for (int i = 0; i < seats.length; ++i) {
            copy[i] = seats[(i + id) % seats.length];
        }

        return copy;
        /*Seat[] copy = new Seat[seats.length];
        int index = Philosopher.currentPhilosopher().getPhilosopherId() % (seats.length);
        System.arraycopy(seats, index, copy, 0, seats.length - index);
        System.arraycopy(seats, 0, copy, index, index);
        return copy;*/
    }

    /**
     * Get the controller.
     *
     * @return the controller.
     */
    public Controller getController() {
        return controller;
    }
}
