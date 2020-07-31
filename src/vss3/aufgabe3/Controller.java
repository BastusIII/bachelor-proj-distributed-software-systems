package vss3.aufgabe3;

/**
 * Controller checks if a philosopher is too greedy.
 * If a philosopher is too greedy and eats 10 times more than an other philosopher,
 * he will have to wait a timePenalty.
 */
public class Controller {
    /**
     * How long the philosopher has to wait if too greedy.
     */
    private final int timePenalty = 5000;
    /**
     * Array of eaten philosopher meals.
     */
    private int[] eatenMeals;
    /**
     * Philosopher max. fatness difference.
     */
    public static final int MAX_MEAL_DIFF = 10;
    /**
     * The minimum of meals eaten by one of the philosophers
     */
    private int minMealCount = 0;

    /**
     * Create an instance of the controller.
     *
     * @param philosopherCapacity the maximum of controllable philosophers.
     */
    public Controller(int philosopherCapacity) {
        eatenMeals = new int[philosopherCapacity];
    }

    /**
     * Check if the method calling philosopher has eaten too much.
     */
    public void mayEat() {
        boolean mayEat = true;
        synchronized (this) {
            int index = Philosopher.currentPhilosopher().getPhilosopherId();
            int minEatenMeals = Integer.MAX_VALUE;
            System.out.print("Meals eaten: ");
            for(int eaten : this.eatenMeals)  {
                minEatenMeals = Math.min(minEatenMeals, eaten);
                System.out.print(eaten+ " ");
            }
            System.out.println();
            mayEat = this.eatenMeals[index] - minEatenMeals <= MAX_MEAL_DIFF;
        }
        if (!mayEat) {
            try {
                System.out.println(Philosopher.currentPhilosopher().toString() + " has to wait for a " +
                        "penalty time because he is too greedy.");
                Thread.sleep(timePenalty);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Log when the method calling philosopher has eaten.
     */
    public void hasEaten() {
        synchronized (this) {
            int index = Philosopher.currentPhilosopher().getPhilosopherId();
            ++this.eatenMeals[index];
        }
    }
}
