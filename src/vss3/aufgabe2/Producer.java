package vss3.aufgabe2;

/**
 * Relentlessly produces {@link vss3.aufgabe2.DataObject}s.
 */
public class Producer extends Thread{
	private int counter = 0;

	@Override
	public void run() {
		while(true) {
			produce();
		}
	}

	/**
	 * Produces a DataObject and adds it to the DataStructure instance.
	 */
	private void produce() {
		DataObject dataObject = new DataObject("Produced by: " + this + " DataObject#" + counter++);
		DataStructure.getInstance().putDataObject(dataObject);
		System.out.println(this + " produced: " + dataObject);
	}

	public int getCounter() {
		return this.counter;
	}
}
