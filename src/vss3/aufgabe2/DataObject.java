package vss3.aufgabe2;

/**
 * DataObject with String data, designed for take in multi-threading-viable data structure.
 */
public class DataObject {

    /** A String representing the content of a data object. */
	private String content;

    /** Flag showing if the data object is already consumed, used for integrity checks. */
	private boolean consumed;

	public DataObject(final String content) {
		this.content = content;
		this.consumed = false;
	}

    /**
     * Nice little String representation.
     * @return String representation of the DataObject.
     */
	public String toString(){
		return "DataObject with content: " + getContent();
	}

    /**
     * Gets the data content.
     * @return The content.
     */
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

    /**
     * Shows if the DataObject is already consumed.
     * @return  true if the object is consumed.
     */
    public boolean isConsumed() {
		return consumed;
	}

    /**
     * Sets if the DataObject is consumed.
     * @param consumed  sets consumed.
     */
	public void setConsumed(final boolean consumed) {
		this.consumed = consumed;
	}
}
