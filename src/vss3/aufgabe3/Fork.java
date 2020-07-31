package vss3.aufgabe3;

/**
 * A Fork that lies on the table.
 * There are as many forks at the table as seats. A Fork ca be in take or free.
 * Forks have IDs, so they can be compared.
 * Taking always the smaller Fork will solve deadlock threads in the program.
 */
public class Fork {

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
     * Fork used or not.
     *
     * @return fork used or not.
     */
    private boolean isUsed() {
        return used;
    }

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
    public synchronized void setUsed() {
        while (this.isUsed()) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("Waiting for " + this + " interrupted!");
            }
        }
        this.used = true;
    }

    /**
     *  Set fork unused.
     */
    public synchronized void setUnused() {
        this.used = false;
        notifyAll();
    }
}
