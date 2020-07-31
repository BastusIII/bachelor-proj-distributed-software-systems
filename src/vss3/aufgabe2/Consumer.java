package vss3.aufgabe2;

/**
 * Relentlessly consumes {@link vss3.aufgabe2.DataObject}s.
 */
public class Consumer extends Thread{

	@Override
	public void run() {
		 while(true) {
			 consume();
		 }
	}

	/**
	 * Consumes a DataObject from the DataStructure instance.
	 */
	private void consume() {
		System.out.println(this + " trying to consume.");
		DataObject dataObject = DataStructure.getInstance().getDataObject();
		checkIntegrity(dataObject);
		dataObject.setConsumed(true);

		System.out.println(this + " consumed: " + dataObject);
	}

	/**
	 * Checks for possible errors due to parallelization.
	 * @param dataObject The DataObject that should be consumed.
	 */
	private void checkIntegrity(final DataObject dataObject) {
		if (dataObject.isConsumed()) {
			throw new ParallelException("DataObject was already consumed!");
		}
	}

}
