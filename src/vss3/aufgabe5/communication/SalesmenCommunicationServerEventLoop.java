package vss3.aufgabe5.communication;

import org.apache.log4j.Logger;
import vss3.aufgabe5.communication.content.NewClientID;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * A Salesmen Communication Server with Event Loop to cyclic check for new message from clients.
 * Avoids maximum thread spammage.
 */
public class SalesmenCommunicationServerEventLoop extends SalesmenCommunicationServer{

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(SalesmenCommunicationServerEventLoop.class);

    /**
     * The time waited between each check cycle.
     */
    private static final int POLLING_INTERVAL = 100;

    /**
     * A new Salesmen Communication Server with Event Loop.
     * @throws IOException If the socket could not be obtained.
     */
    public SalesmenCommunicationServerEventLoop() throws IOException {
        super();

        /* Thread for accepting new clients that are trying to connect. */
        new Thread() {

            @Override
            public void run() {
                while (true) {
                    try {
                        Socket newClient = socket.accept();
                        /* increase client number, to get the new clients id */
                        numberOfClients++;
                        /* Add the new client to known sockets. */
                        synchronized (clientSockets) {
                            clientSockets.put(numberOfClients, newClient);
                        }
                        /* Add the new clients stream reader. */
                        synchronized (clientReaders) {
                            clientReaders.add(new BufferedReader(new InputStreamReader(newClient.getInputStream())));
                        }
                        LOGGER.info("New client bound. ID = " + numberOfClients );
                        /*  Sending new client id  */
                        try {
                            SalesmenCommunicationMessage clientIdMessage =  new SalesmenCommunicationMessage();
                            clientIdMessage.setContent(new NewClientID(numberOfClients));
                            mapper.writeValue(newClient.getOutputStream(), clientIdMessage);
                        } catch (IOException e) {
                            LOGGER.error("Could not send the address to client with number: "
                                    + numberOfClients + " : " + e.getMessage());
                        }
                    } catch (IOException e) {
                        LOGGER.error("Could not accept new client: " + e.getMessage());
                    }

                }
            }
        }.start();


        /* Thread with the event loop to check for new messages. */
        new Thread() {

            @Override
            public void run() {
                while (true) {
                    if(LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Checking for new messages.");
                    }
                    /* Synchronized  list access to avoid concurrent modification.*/
                    synchronized (clientReaders) {
                        for (BufferedReader client : clientReaders) {
                            try {
                                /* Read all message from the client and add them to the message queue. */
                                while (client.ready()) {
                                    SalesmenCommunicationMessage message = mapper.readValue(client.readLine(), SalesmenCommunicationMessage.class);
                                    synchronized (messages) {
                                        messages.add(message);
                                        messages.notifyAll();
                                    }
                                }
                            } catch (IOException e) {
                                /* Client socket is broken! Let's never read from him again. */
                                LOGGER.error("Could not read from client: " + client + ": " + e);
                                LOGGER.error("Removing: " + client + ".");
                                clientReaders.remove(client);
                            }
                        }

                    }
                    /* Socket check terminated now sleep until the next one. */
                    try {
                        sleep(POLLING_INTERVAL);
                    } catch (InterruptedException e) {
                        LOGGER.error("Polling service could not sleep: " + e);
                    }
                }

            }
        }.start();
    }
}
