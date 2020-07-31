package vss3.aufgabe4;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IPhilosopher extends Remote{
	
	/**
	 * Set the philisoph as to greedy so he has to stop eating so much.
	 */
	public void isTooGreedy() throws RemoteException;
	
	/**
	 * Get the Id of the philosoph.
	 * 
	 * @return	ID of the philosoph.
	 */
	public int getPhilosopherId() throws RemoteException;
	
	/**
	 * Get name of the philosoph in the registry.
	 * 
	 * @return	Name of the philosph in the registry.
	 */
	public String getPhilosopherName() throws RemoteException;
	
	
}
