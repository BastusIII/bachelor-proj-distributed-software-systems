package vss3.aufgabe5.communication.content;

/**
 * Content with new client id.
 */
public class NewClientID implements MessageContent{

    /**
     * The client id.
     */
    private int id;

    public NewClientID() {}

    public NewClientID(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
