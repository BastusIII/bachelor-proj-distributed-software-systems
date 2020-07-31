package vss3.aufgabe4;

import java.io.Serializable;
import java.rmi.RemoteException;

import org.apache.log4j.Logger;

/**
 * A Seat on the table and one of the resources a Philosopher needs to eat.
 */
public class Seat implements ISeat, Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * The Logger.
     */
    public static final Logger LOGGER = Logger.getLogger(Seat.class);
    /**
     * The ID of the instance.
     */
    private final int id;
    /**
     * The table this seat is placed at.
     */
    private final IAgent agent;
    /**
     * The philosopher, that has taken the seat.
     */
    private String owner = null;
    /**
     * The name of the Seat in the registry.
     */
    private final String name;
    
	/**
	 * Name of a Seat.
	 * @param id	Number of the Seat.
	 * @return		Name of the Seat.
	 */
    public static String getNameForSeat(int id){
    	return "Seat"+id;
    }

    /**
     * Create an instance declaring left and right fork.
     *
     * @param leftFork  the left fork.
     * @param rightFork the right fork.
     */
    public Seat(final int id, final Agent agent) {
    	this.id = id;
        this.agent = agent;
        this.name = getNameForSeat(id);
        LOGGER.debug("New Seat is build.");
    }
    
    @Override
    public String getSeatName(){
    	return name;
    }

    @Override
    synchronized public boolean take(String name) {

        if (this.owner != null) {
            LOGGER.debug(name + " tried to take seat " +
                    this.id + ".");
            return false;
        }
        this.owner = name;
        LOGGER.debug(name + " took " + this + ".");
        return true;
    }
    
    @Override
    synchronized public void waitForEmptySeat(){
    	while(this.owner != null){
    		try {
				wait();
			} catch (InterruptedException e) {
				LOGGER.error(e);
			}
    	}
    }

    @Override
    synchronized public void giveBack(String name){
        if (name == this.owner) {
            this.owner = null;
            LOGGER.debug(name + " gave back back " + this + ".");
        } else {
            LOGGER.error(name + " tried to give back " +
                    this + " illegally.");
        }
        this.notify();
    }

    @Override
    public String toString() {
        return "seat " + this.id;
    }
}