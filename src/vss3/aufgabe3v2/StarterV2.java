package vss3.aufgabe3v2;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.util.LinkedList;
import java.util.List;

/**
 * StarterV2 creates the Table the Controller and the Philosophers based on commandline arguments.
 */
public class StarterV2 {

    /**
     * The Logger.
     */
    public static final Logger LOGGER = Logger.getLogger(StarterV2.class);

    /**
     * The main method that starts our philosophers and creates all resources.
     *
     * @param args cmdline params.
     * @throws InterruptedException
     */
    public static void main(String... args) throws InterruptedException {

        int numberOfSeats = Integer.parseInt(args[0]);
        int numberOfPhilosophers = Integer.parseInt(args[1]);
        int maxOccupiedTime = Integer.parseInt(args[2]);

        //Controller can handle as many philosophers as given in the commandline
        Controller controller = new Controller(numberOfPhilosophers);
        Table table = new Table(controller, numberOfSeats);
        List<Philosopher> philosophers = new LinkedList<>();

        for (int i = 0; i < Integer.parseInt(args[1]); ++i) {
            //first philosopher is hungry
            if (i == 0) {
                philosophers.add(new Philosopher(table, true, maxOccupiedTime));
                continue;
            }
            philosophers.add(new Philosopher(table, maxOccupiedTime));
        }
        for (Philosopher p : philosophers) {
            p.join();
        }
        for (Philosopher p : philosophers) {
            p.start();
        }
    }
}
