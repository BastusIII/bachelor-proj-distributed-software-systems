package vss3.aufgabe5;

import org.apache.log4j.Logger;
import vss3.aufgabe4.MainMaster;
import vss3.aufgabe5.communication.content.TableContent;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * The SalesmenServerMain is a simple console user interface and lets the user calculate the solution of the traveling salesmen problem.
 *
 * @author Sebastian Stumpf
 */
public class SalesmenServerMain {

    private static final Logger LOGGER = Logger.getLogger(SalesmenServerMain.class);
    private static final Scanner scanner = new Scanner(System.in);
    private static SalesmenServer salesmenServer;

    /**
     * Submenue. Enter start city and amount of workers.
     */
    private static void startTravelingSalesman() {
        int workers;
        int index;
        int[] cityIndices = null;
        System.out.println("Please enter the amount of workers...");
        workers = scanner.nextInt();
        scanner.nextLine();
        do {
            System.out.println("Please enter the the cities to visit...");
            System.out.println(".. with index separated by comma.");
            String cityIndicesString = scanner.nextLine();
            List<Integer> cityList = new LinkedList<>();
            TableContent fullContent = TableContent.getFullContent();
            for(String cityName: cityIndicesString.split(",")) {
                cityList.add(fullContent.getIndex(cityName));
            }
            if (cityList.size() > 3) {
                cityIndices = new int[cityList.size()];
                for(int i = 0; i < cityIndices.length; i++) {
                    cityIndices[i] = cityList.get(i);
                }
            }  else {
                System.out.println("Could not interpret your indices.");
            }
        } while(cityIndices == null);

        do {
            System.out.println("Please enter the index of the start city...");
            String city = scanner.nextLine();

            index = Integer.parseInt(city);
            if (index < 0) {
                System.out.println("City is not an index.");
            }
        } while (index < 0);
        salesmenServer.startTravelingSalesman(workers, index, cityIndices);
    }

    /**
     * Main class fur user interaction.
     */
    public static void main(String... args) {
        try {
            salesmenServer = new SalesmenServer();
            String city = "not initialized yet";

            boolean exit = false;
            while (!exit) {
                System.out.println("Salesmen server options:");
                System.out.println("1) Start traveling salesman calculation");
                System.out.println("2) Print result of last calculation");
                //System.out.println("3) Reset server");
                System.out.println("3) Exit");
                int input = scanner.nextInt();
                scanner.nextLine();
                switch (input) {
                    case 1:
                        startTravelingSalesman();
                        break;
                    case 2:
                        System.out.println("The solution of the traveling salesmen problem with the starting city " + salesmenServer.getReadableStartCity() + " is:");
                        System.out.println("The optimal round trip: " + salesmenServer.getReadableShortestPath());
                        System.out.println("The distance: " + salesmenServer.getReadableMinimum());
                        break;
                    /*case 3:
                        salesmenServer.reset();
                        System.out.println("The Server was reset.");
                        break;*/
                    case 3:
                        exit = true;
                        break;
                    default:
                        System.out.println("dafuq?");
                }
            }
        } catch (IOException e) {
            LOGGER.error("Salesmen server could not be started: " + e.getMessage());
        }
    }
}
