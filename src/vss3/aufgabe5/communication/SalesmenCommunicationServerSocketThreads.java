package vss3.aufgabe5.communication;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import vss3.aufgabe5.communication.content.NewClientID;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * A Salesmen Communication Server with Socket Threads. Runs a thread for each individual socket.
 */
public class SalesmenCommunicationServerSocketThreads extends SalesmenCommunicationServer{

    private static final Logger LOGGER = Logger.getLogger(SalesmenCommunicationServerEventLoop.class);

    public SalesmenCommunicationServerSocketThreads() throws IOException {
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
                        synchronized (clientSockets) {
                            clientSockets.put(numberOfClients, newClient);
                        }
                        final BufferedReader clientReader = new BufferedReader(new InputStreamReader(newClient.getInputStream()));
                        synchronized (clientReaders) {
                            clientReaders.add(clientReader);
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

                         /* Thread to check this new client socket for new messages.. */
                        new Thread() {

                            /* Each thread has its own mapper. */
                            private final ObjectMapper myMapper = new ObjectMapper();

                            @Override
                            public void run() {
                                myMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
                                myMapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
                                while (true) {
                                    try {
                                        SalesmenCommunicationMessage message = myMapper.readValue(clientReader.readLine(), SalesmenCommunicationMessage.class);
                                        synchronized (messages) {
                                            messages.add(message);
                                            messages.notifyAll();
                                        }

                                    } catch (IOException e) {
                                        LOGGER.error("Could not accept new client: " + e.getMessage());
                                    }
                                }
                            }
                        }.start();
                    } catch (IOException e) {
                        LOGGER.error("Could not accept new client: " + e.getMessage());
                    }

                }
            }
        }.start();

    }
}
