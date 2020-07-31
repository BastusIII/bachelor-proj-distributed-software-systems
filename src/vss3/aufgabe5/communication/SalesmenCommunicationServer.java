package vss3.aufgabe5.communication;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import vss3.aufgabe5.communication.content.MessageContent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Salesmen Communication Server for communication with the clients.
 */
public abstract class SalesmenCommunicationServer {

    /**
     * The logger for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(SalesmenCommunicationServer.class);

    /**
     * The port where the server listens for clients.
     */
    public static final int SCS_PORT = 53464;

    /**
     * The client address which leads to the message being broadcast.
     */
    public static final int BROADCAST_ADDRESS = 0;

    /**
     * The socket used for communication.
     */
    protected final ServerSocket socket;

    /**
     * The readers for the clients input streams.
     */
    protected final List<BufferedReader> clientReaders = new LinkedList<>();

    /**
     * The sockets for all clients. Indexed by client address.
     */
    protected final Map<Integer, Socket> clientSockets = new HashMap<>();

    /**
     * The message queue for client messages.
     */
    protected final List<SalesmenCommunicationMessage> messages = new LinkedList<>();

    /**
     * The mapper used for de-/serializing messages.
     */
    protected ObjectMapper mapper = new ObjectMapper();

    /**
     * The number of clients that connected to the server.
     */
    protected int numberOfClients = 0;

    /**
     * A server with configured mapper and socket.
     * @throws IOException If the port could not be obtained.
     */
    public SalesmenCommunicationServer() throws IOException {
        mapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
        mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
        socket = new ServerSocket(SCS_PORT);
    }

    /**
     * Gets a message from the queue or blocks until a new message is available.
     * @return The message from a client.
     */
    public SalesmenCommunicationMessage getMessage() {
        SalesmenCommunicationMessage message = null;
        /* Retrieve the message from queue synchronized to avoid inconsistent list states */
        synchronized (messages) {
            while (messages.isEmpty()) {
                try {
                    /* Wait for a message to become available. */
                    messages.wait();
                } catch (InterruptedException e) {
                    LOGGER.error("getMessage(): Waiting for messages was interrupted: " + e);
                }
            }
            if (!messages.isEmpty()) {
                message =  messages.remove(0);
            }
        }

        return message;
    }

    /**
     * Send a message the client whose address is specified in the address field.
     * @param message The message to send.
     * @throws IOException If the messge could not be sent.
     */
    public void sendMessage(final SalesmenCommunicationMessage message) throws IOException {
        /* Check for broadcasts. */
        if(message.getAddress() == BROADCAST_ADDRESS) {
            broadCastContent(message.getContent());
            return;
        }
        Socket clientSocket = clientSockets.get(message.getAddress());
        if(clientSocket == null) {
            throw new IOException("Could not send message to: " + message.getAddress() + ". Could not find socket.");
        }
        OutputStream outputStream = clientSocket.getOutputStream();
        if(outputStream == null) {
            throw new IOException("Could not send message to: " + message.getAddress() + ". Could get output stream.");
        }
        mapper.writeValue(outputStream, message);
        /* Send new line, to signal end of message. */
        outputStream.write('\n');
        outputStream.flush();
    }

    /**
     * Broadcasts the content to all known clients.
     * @param content The content broadcast.
     * @throws IOException If the message could not be sent to all clients.
     */
    public void broadCastContent(final MessageContent content) throws IOException {
        for(int address = 1; address <= numberOfClients; address++) {
            SalesmenCommunicationMessage clientMessage = new SalesmenCommunicationMessage(content);
            clientMessage.setAddress(address);
            sendMessage(clientMessage);
        }
    }

}
