package vss3.aufgabe5.communication;

import vss3.aufgabe5.communication.content.MessageContent;

/**
 * A message used for communication between salesmen.
 */
public class SalesmenCommunicationMessage {

    /**
     * The address where the message should be sent to or is from.
     */
    private int address;

    /**
     * The content of the message.
     */
    private MessageContent content;

    /**
     * A new salesmen message.
     */
    public SalesmenCommunicationMessage() {}

    /**
     * A new salesman message with content.
     * @param messageContent The content of the message.
     */
    public SalesmenCommunicationMessage(MessageContent messageContent) {
        this.content = messageContent;
    }

    public MessageContent getContent() {
        return content;
    }

    public void setContent(MessageContent content) {
        this.content = content;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }
}
