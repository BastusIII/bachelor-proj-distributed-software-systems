package vss3.aufgabe2;

/**
 * Exception for parallelization issues.
 */
public class ParallelException extends RuntimeException {
	public ParallelException(final String s) {
		super(s);
	}
}
