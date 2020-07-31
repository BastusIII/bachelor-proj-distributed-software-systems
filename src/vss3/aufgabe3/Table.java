package vss3.aufgabe3;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The class Table implements a Table the Philosophers can eat at.
 * The table also controls the feature, that hungry Controllers may eat more often.
 */
public class Table {

    /**
     * Monitor for usage of seats.
     */
    public static Object SEAT_MONITOR = new Object();
    /**
     * Controller for table.
     */
    private Controller controller;
    /**
     * List of free seats.
     */
    private BlockingQueue<Seat> freeSeats = new LinkedBlockingQueue<>();
    /**
     * List of waiting Philosophers.
     */
    private BlockingQueue<Philosopher> waitingPhilosophers = new LinkedBlockingQueue<>();
    /**
     * List of waiting hungry Philosophers with higher Priority.
     */
    private BlockingQueue<Philosopher> waitingHungryPhilosophers = new LinkedBlockingQueue<>();

    /**
     * Create an instance of table that initialises all the seats and forks.
     *
     * @param seats the number of seats at the table.
     */
    public Table(Controller controller, int seats) {
        this.controller = controller;
        List<Fork> forks = new LinkedList<>();
        for (int i = 0; i < seats; ++i) {
            forks.add(new Fork());
        }
        for (int i = 0; i < seats - 1; ++i) {
            this.freeSeats.add(new Seat(forks.get(i), forks.get(i + 1)));
        }
        this.freeSeats.add(new Seat(forks.get(seats - 1), forks.get(0)));

    }

    /**
     * Will occupy a seat, meaning the resource is blocked.
     * Hungry philosophers get seats with a higher priority than normal philosophers.
     * The controller is asked if the philosopher may instantly request a seat,
     * or has to wait (because he is too greedy).
     *
     * @return the occupied seat.
     */
    public Seat getSeat() throws InterruptedException {

        Philosopher philosopher = Philosopher.currentPhilosopher();
        Seat seat = null;
        controller.mayEat();
        if (philosopher.isHungry()) {
            waitingHungryPhilosophers.add(philosopher);
            synchronized (SEAT_MONITOR) {
                while (!(philosopher == waitingHungryPhilosophers.element())) {
                    SEAT_MONITOR.wait();
                }
                seat = freeSeats.take();
            }
            waitingHungryPhilosophers.remove();
        } else {
            waitingPhilosophers.add(philosopher);
            synchronized (SEAT_MONITOR) {
                if (!waitingHungryPhilosophers.isEmpty()) {
                    SEAT_MONITOR.wait();
                }
                while (!(philosopher == waitingPhilosophers.element())) {
                    SEAT_MONITOR.wait();
                    this.controller.mayEat();
                }
                seat = freeSeats.take();
            }
            waitingPhilosophers.remove();
        }

        System.out.println(Philosopher.currentPhilosopher().toString() + " sits down on " + seat + ".");
        return seat;

    }

    /**
     * Will release a seat, meaning the resource is no longer blocked.
     * @param seat the seat to release.
     */
    public void releaseSeat(Seat seat) {

        this.freeSeats.add(seat);
        synchronized (SEAT_MONITOR) {
            SEAT_MONITOR.notifyAll();
        }
        controller.hasEaten();
        System.out.println(Philosopher.currentPhilosopher().toString() + " released " + seat + ".");
    }
}
