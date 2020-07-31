package vss3.aufgabe3v2;

import org.apache.log4j.Logger;

/**
 * A Fork that lies on the table.
 * There are as many forks at the table as seats. A Fork ca be in take or free.
 * Forks have IDs, so they can be compared.
 * Taking always the smaller Fork will solve deadlock threads in the program.
 */
public class Fork {

    /**
     * The Logger.
     */
    public static final Logger LOGGER = Logger.getLogger(Fork.class);
    /**
     * Static increasing value for each new instance.
     */
    private static int counter = 0;
    /**
     * The ID of the instance.
     */
    private final int id = counter++;

    private boolean used = false;

    /**
     * Get the id.
     *
     * @return the id.
     */
    public int getId() {
        return id;
    }

    /**
     * Set fork used.
     */
    public synchronized void take() {
        while (this.used) {
            try {
                wait();
            } catch (InterruptedException e) {
                LOGGER.error("Waiting for " + this + " interrupted!");
            }
        }
        this.used = true;
    }

    /**
     * Set fork unused.
     */
    public synchronized void giveBack() {
        this.used = false;
        notifyAll();
    }
}
