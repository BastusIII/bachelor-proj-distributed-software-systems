package vss3.aufgabe3v2;

import org.apache.log4j.Logger;

import java.util.Arrays;

/**
 * Controller checks if a philosopher is too greedy.
 * If a philosopher is too greedy and eats 10 times more than an other philosopher,
 * he will have to wait a timePenalty.
 */
public class Controller {

    /**
     * The Logger.
     */
    public static final Logger LOGGER = Logger.getLogger(Controller.class);
    /**
     * Array of eaten philosopher meals.
     */
    private final int[] takenSeatsArray;
    /**
     * Philosopher max. fatness difference.
     */
    private static final int MAX_MEAL_DIFF = 10;

    /**
     * Create an instance of the controller.
     *
     * @param philosopherCapacity the maximum of controllable philosophers.
     */
    public Controller(int philosopherCapacity) {
        takenSeatsArray = new int[philosopherCapacity];
    }

    /**
     * Check if the method calling philosopher has eaten too much.
     */
    public void philosopherTookSeat() {

        /*
      The sorted version of takenSeatsArray.
     */
        int[] sortedTakenSeatsArray = takenSeatsArray.clone();
        Arrays.sort(sortedTakenSeatsArray);
        takenSeatsArray[Philosopher.currentPhilosopher().getPhilosopherId()]++;
        if (takenSeatsArray[Philosopher.currentPhilosopher().getPhilosopherId()] >
                sortedTakenSeatsArray[0] + MAX_MEAL_DIFF) {
            Philosopher.currentPhilosopher().isTooGreedy();
        }
        LOGGER.info("Philosopher seat count: " + Arrays.toString(this.takenSeatsArray) +
                "; Minimum: " + sortedTakenSeatsArray[0]);
    }
}
