package vss3.aufgabe4;

/**
 * Information for philosopher remote.
 */
public class PhilosopherInformation extends RemoteInformation{

	private boolean isHungry = false;

	private int maxOccupationTime = 1000;

	PhilosopherInformation(final String name, final String agentName, final int number) {
		super(IPhilosopher.class, name, agentName, number);
	}

	public boolean isHungry() {
		return isHungry;
	}

	public void setHungry(boolean hungry) {
		isHungry = hungry;
	}

	public int getMaxOccupationTime() {
		return maxOccupationTime;
	}

	public void setMaxOccupationTime(int maxOccupationTime) {
		this.maxOccupationTime = maxOccupationTime;
	}
}
