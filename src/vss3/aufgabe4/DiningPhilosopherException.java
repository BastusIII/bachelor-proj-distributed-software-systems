package vss3.aufgabe4;

/**
 * Exception class for dining philosophers.
 */
public class DiningPhilosopherException extends Exception{
	/**
	 * DiningPhilosopherException with custom message.
	 * @param message The custom message.
	 */
	public DiningPhilosopherException(String message) {
		super(message);
	}

	/**
	 * DiningPhilosopherException with throwable.
	 * @param throwable The custom message.
	 */
	public DiningPhilosopherException(Throwable throwable) {
		super(throwable);
	}

}
