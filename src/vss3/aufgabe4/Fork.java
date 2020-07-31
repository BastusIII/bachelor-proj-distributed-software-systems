package vss3.aufgabe4;

import java.io.Serializable;

import org.apache.log4j.Logger;

/**
 * A Fork that lies on the table.
 * There are as many forks at the table as seats. A Fork ca be in take or free.
 * Forks have IDs, so they can be compared.
 * Taking always the smaller Fork will solve deadlock threads in the program.
 */
public class Fork implements IFork, Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * The Logger.
     */
    public static final Logger LOGGER = Logger.getLogger(Fork.class);
    /**
     * The ID of the instance.
     */
    private final int id;
    /**
     * The name of the fork in the registry.
     */
    private final String name;
    /**
     * The Agent instance.
     */
    private IAgent agent;
    /**
     * The name of the Philosoph who takes the fork.
     */
    private String owner = null;
    /**
	 * Name of a Fork.
	 * @param id	Number of the Fork.
	 * @return		Name of the Fork.
	 */
    public static String getNameForFork(int id){
    	return "Fork"+id;
    }

    /**
     * Constructor
     * 
     * @param id	The id of the fork.
     * @param agent	The agent the fork is be created on.
     */
    public Fork(final int id, final Agent agent){ 
    	this.id = id;
    	this.agent = agent;
    	this.name = getNameForFork(id);
    	LOGGER.debug("A new wild Fork appears.");
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
    public synchronized void take(String name) {
        while (owner != null) {
            try {
                wait();
            } catch (InterruptedException e) {
                LOGGER.error("Waiting for " + this + " interrupted!");
            }
        }
        this.owner = name;
    }

    /**
     * Set fork unused.
     */
    public synchronized void giveBack(String name) {
    	if(owner != null && owner.equals(name)){
    		this.owner = null;
    	}
        notifyAll();
    }
	@Override
	public String getForkName() {
		return name;
	}
}
