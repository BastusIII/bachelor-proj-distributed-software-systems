package vss3.aufgabe5.communication.content;

/**
 * Request for a new task.
 */
public class TaskRequest implements MessageContent{

    /**
     * The address of the client requesting a new task.
     */
    private int clientAddress;

    /**
     * For improved politeness.
     */
    private String please = "Please!";

    public int getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(int clientAddress) {
        this.clientAddress = clientAddress;
    }

    public String getPlease() {
        return please;
    }

    public void setPlease(String please) {
        this.please = please;
    }
}
