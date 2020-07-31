package vss3.aufgabe4;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Scanner;

/**
 * Start and Control the Master and offer a command user interface to interact.
 */
public class MainMaster {

	private static Logger LOGGER = Logger.getLogger(MainMaster.class);

	public static void main(String... args) throws IOException {
		Master master = new Master();
		int maxOccupiedTime = 1000;
		Scanner scanner = new Scanner(System.in);

		boolean exit = false;
		while(!exit){
			System.out.println("Master-Control:");
			System.out.println("1) Create Philosopher");
			System.out.println("2) Create Hungry-Philosopher");
			System.out.println("3) Create Seat");
			System.out.println("4) Remove Philosopher");
			System.out.println("5) Remove Seat");
			System.out.println("6) Set maximum occupation time");
			System.out.println("7) Exit");
			int input = scanner.nextInt();

			switch(input) {
				case 1:
					try {
						master.addPhilosopher(false, maxOccupiedTime);
					} catch (DiningPhilosopherException e) {
						LOGGER.error(e);
					}
					break;
				case 2:
					try {
						master.addPhilosopher(true, maxOccupiedTime);
					} catch (DiningPhilosopherException e) {
						LOGGER.error(e);
					}
					break;
				case 3:
					try {
						master.addSeat();
					} catch (DiningPhilosopherException e) {
						LOGGER.error(e);
					}
					break;
				case 4:	master.removePhilosopher();
						break;
				case 5:	master.removeSeat();
						break;
				case 6:	System.out.print("Please enter the new time:");
						maxOccupiedTime = scanner.nextInt();
						break;
				case 7:	exit = true;
						break;
				default:
					System.out.println("dafuq?");
			}
		}
	}
}
