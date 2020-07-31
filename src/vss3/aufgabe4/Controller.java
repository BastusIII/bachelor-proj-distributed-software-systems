package vss3.aufgabe4;

import org.apache.log4j.Logger;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.Arrays;

/**
 * Controller checks if a philosopher is too greedy. If a philosopher is too
 * greedy and eats 10 times more than an other philosopher, he will have to wait
 * a timePenalty.
 */
public class Controller implements IController {

	/**
	 * The Logger.
	 */
	public static final Logger LOGGER = Logger.getLogger(Controller.class);
	/**
	 * Array of eaten philosopher meals.
	 */
	private int[] takenSeatsArray;
	/**
	 * Philosopher max. fatness difference.
	 */
	private static final int MAX_MEAL_DIFF = 10;

	/**
	 * Amount of Philosophers
	 */
	private int amountOfPhilosophes;

	/**
	 * The registry on the server.
	 */
	private final Registry registry;

	/**
	 * Create an instance of the controller.
	 * 
	 * @param philosopherCapacity
	 *            the maximum of controllable philosophers.
	 */
	public Controller(int philosopherCapacity, Registry registry) {
		takenSeatsArray = new int[philosopherCapacity];
		this.registry = registry;
	}

	/**
	 * Set the number of Philosophes.
	 * 
	 * @param amount
	 */
	public void setNumberOfPhilosophes(int amount) {
		if (amount != amountOfPhilosophes) {
			int[] tmp = new int[amount];
			int maxEntrys = Math.min(amount, takenSeatsArray.length);
			for (int i = 0; i < maxEntrys; i++) {
				tmp[i] = takenSeatsArray[i];
			}
			if (amountOfPhilosophes < amount) {
				tmp[amount - 1] = tmp[(int) (Math.random() * (amount - 2))];
			}
			amountOfPhilosophes = amount;
			takenSeatsArray = tmp;
		}
	}

	@Override
	/**
	 * Check if the method calling philosopher has eaten too much.
	 */
	public void philosopherStartsEating(int philosopherId,
			String philosopherName) {
		try{
		int[] sortedTakenSeatsArray = takenSeatsArray.clone();
		Arrays.sort(sortedTakenSeatsArray);
		takenSeatsArray[philosopherId - 1]++;
		if (takenSeatsArray[philosopherId - 1] > sortedTakenSeatsArray[0]
				+ MAX_MEAL_DIFF) {
			LOGGER.info(philosopherName + " was to greedy!");
			IPhilosopher phil = null;
			try {
				phil = (IPhilosopher) registry.lookup(philosopherName);
			} catch (AccessException e) {
				LOGGER.error(e);
			} catch (RemoteException e) {
				LOGGER.error(e);
			} catch (NotBoundException e) {
				LOGGER.error(e);
			}
			if (phil != null) {
				try {
					phil.isTooGreedy();
				} catch (RemoteException e) {
					LOGGER.error(e);
				}
			}
		}
		LOGGER.info("Philosopher seat count: "
				+ Arrays.toString(this.takenSeatsArray) + "; Minimum: "
				+ sortedTakenSeatsArray[0]);
		}catch(ArrayIndexOutOfBoundsException e){
			//Do nothing, it's ok... Philosophes are sometimes very slow..
		}
	}

}
