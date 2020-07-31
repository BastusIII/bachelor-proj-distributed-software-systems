package vss3.aufgabe5.communication;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import vss3.aufgabe5.communication.content.MessageContent;
import vss3.aufgabe5.communication.content.NewClientID;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Observable;

/**
 * SalesmenCommunicationClient for communication with the server.
 */
public class SalesmenCommunicationClient extends Observable {

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(SalesmenCommunicationClient.class);

    /**
     * The output stream to the server socket.
     */
    private OutputStream outputStream;

    /**
     * The reader attached to the input stream from the server.
     */
    private BufferedReader reader;

    /**
     * Jackson mapper to de-/serialize the json messages.
     */
    private ObjectMapper mapper = new ObjectMapper();

    /**
     * The address of this client.
     */
    private int clientAddress;


    /**
     * Creates a new client that connects to the server at given address.
     * @param Servername Networkname of the server.
     * @throws IOException If the client cannot connect.
     */
    public SalesmenCommunicationClient(final String Servername) throws IOException {
        /* Configure the mapper to not automatically close the streams. */
        mapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
        mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
        /* Connect to the server. */
        Socket socket = new Socket(Servername, SalesmenCommunicationServer.SCS_PORT);
        outputStream = socket.getOutputStream();
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        receiveClientAddress();

        final SalesmenCommunicationClient thisClient = this;

        /* Create thread that notifies the observers about new messages. */
        new Thread() {
            @Override
            public void run() {
                while(true) {
                    thisClient.notifyObservers(thisClient.getMessage().getContent());
                }
            }
        }.start();
    }

    /**
     * Receives a address from the server and sets it for the client.
     */
    private void receiveClientAddress() {
        try {
            SalesmenCommunicationMessage message = mapper.readValue(reader, SalesmenCommunicationMessage.class);
            if(message.getContent().getClass() == NewClientID.class) {
                NewClientID clientId = (NewClientID) message.getContent();
                this.clientAddress = clientId.getId();
                LOGGER.info("Received new client client address: " + this.clientAddress);
            } else {
                LOGGER.error("Tried to receive new client address, got a message, opened it, prayed for a nice little new client address, but there was none.");
            }
        } catch (IOException e) {
            LOGGER.error("Could not receive new client client address: " + e);
        }
    }

    /**
     * Get a message from the server, blocks until a message is available.
     * @return The message from the server.
     */
    private SalesmenCommunicationMessage getMessage() {
        SalesmenCommunicationMessage message = new SalesmenCommunicationMessage();
        try {
            message = mapper.readValue(reader.readLine(), SalesmenCommunicationMessage.class);
            this.setChanged();
        } catch (IOException e) {
            LOGGER.error("Could not receive message: " + e);
        }

        return message;
    }

    /**
     * Send a message to the server.
     * @param message The message to send.
     * @throws IOException If the message could not be sent.
     */
    private void sendMessage(final SalesmenCommunicationMessage message) throws IOException {
        mapper.writeValue(outputStream, message);
        outputStream.write('\n');
        outputStream.flush();
    }

    /**
     * Sends content to the server and authenticates the message with the clients id.
     * @param content The content to send.
     * @throws IOException If The message can not be sent.
     */
    public void sendContent(final MessageContent content) throws IOException {
        SalesmenCommunicationMessage message = new SalesmenCommunicationMessage(content);
        message.setAddress(this.clientAddress);
        sendMessage(message);
    }

}
