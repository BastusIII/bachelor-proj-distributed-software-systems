package vss3.aufgabe3;

/**
 * StarterV2 creates the Table the Controller and the Philosophers based on commandline arguments.
 */
public class Starter {

    public static void main(String... args) {

        //Controller can handle as many philosophers as given in the commandline
        Controller controller = new Controller(Integer.parseInt(args[1]));
        Table table = new Table(controller, Integer.parseInt(args[0]));

        for (int i = 0; i < Integer.parseInt(args[1]); ++i) {
            //first philosopher is hungry
            if (i == 0) {
                new Philosopher(table, true).start();
                continue;
            }
            new Philosopher(table).start();
        }
    }
}
