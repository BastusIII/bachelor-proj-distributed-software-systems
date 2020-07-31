package vss3.aufgabe2;

/**
 * Starts multi-threaded producer / consumer situation.
 */
public class Starter {

	public static void main(final String... args) {

		final int numberOfProducers = Integer.parseInt(args[0]);
		final int numberOfConsumers = Integer.parseInt(args[1]);
		for(int i = 0; i < numberOfProducers; i++) {
			new Producer().start();
		}
		for(int i = 0; i < numberOfConsumers; i++) {
			new Consumer().start();
		}

	}
}
