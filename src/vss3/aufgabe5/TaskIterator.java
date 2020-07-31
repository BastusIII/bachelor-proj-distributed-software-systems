package vss3.aufgabe5;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * TaskIterator
 * 
 * Generates start Paths for an amount of Tasks.
 * 
 * @author Waldleitner
 *
 */
public class TaskIterator implements Iterator<List<Integer>> {

	/**
	 * Maximum depth of the start Path
	 */
	private int maxDepth;
	/**
	 * Start city of the round trip.
	 */
	private int startCity;
	/**
	 * Amount of cities in the round trip.
	 */
	private int amountOfCities;
	/**
	 * Counter for every depth.
	 */
	private int[] depthCounter;
	/**
	 * Mapper from the values 0,1,2,... to the city numbers.
	 */
	private int[] mapper;

	/**
	 * Next start path.
	 */
	private List<Integer> next = null;

	/**
	 * Constructor
	 * 
	 * @param maxDepth			Maximum depth of the start path.
	 * @param amountOfCities	Amount of cities in the round trip.
	 * @param startCity			Start city of the the round trip.
	 */
	public TaskIterator(int maxDepth, int amountOfCities, int startCity) {
		this.maxDepth = maxDepth;
		this.startCity = startCity;
		this.amountOfCities = amountOfCities - 1;
		this.depthCounter = new int[maxDepth];
		
		//Initialization of first start path
		for (int i = 0; i < maxDepth; i++) {
			depthCounter[i] = i;
		}
		depthCounter[depthCounter.length-1]--;
		//Initialization of the cities mapper
		mapper = new int[amountOfCities];
		int counter = 0;
		for (int i = 0; i < mapper.length; i++) {
			if (counter == startCity) {
				counter++;
			}
			mapper[i] = counter;
			counter++;
		}
		getNext();
	}

	@Override
	public boolean hasNext() {
		return (next == null) ? false : true;
	}

	@Override
	public List<Integer> next() {
		List<Integer> result = next;
		next=null;
		getNext();
		return result;
	}

	/**
	 * Calculates the next start path.
	 */
	private void getNext() {
		next = null;
		do{
			if(!increase()){
				return;
			}
		}
		while (checkSameCities());
		
		next = new ArrayList<Integer>();
		next.add(startCity);
		for (int city : depthCounter) {
			next.add(mapper[city]);
		}
	}

	/**
	 * Increase the path to the next permutation
	 * 
	 * @return	true if a increase was possible
	 * 			false if there no next permutation
	 */
	private boolean increase() {
		for (int i = depthCounter.length - 1; i >= 0; i--) {
			depthCounter[i]++;
			if (depthCounter[i] < amountOfCities) {
				return true;
			}
			else {
				depthCounter[i] = 0;
			}
		}
		return false;
	}

	/**
	 * Checks if a city is used more often than once.
	 * 
	 * @return	true	if one or more cities are not used only once.
	 * 			false	if each city is only used once.
	 */
	private boolean checkSameCities() {
		boolean sameCities = false;
		for (int i = 0; i < depthCounter.length; i++) {
			for (int j = i + 1; j < depthCounter.length; j++)
				if (depthCounter[i] == depthCounter[j]) {
					return true;
				}
		}
		return sameCities;
	}

	@Override
	public void remove() {
		// nothing to do...
	}

}
