package vss3.aufgabe4;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ISeat extends Remote{

	/**
	 * Take a Seat
	 * 
	 * @param name	Name of the philosoph who wants to take the seat.
	 * @return		true if philosoph can take the seat.
	 * 				false if seat is already taken.
	 */
	public boolean take(String name) throws RemoteException;
	
	/**
	 * Give back a seat
	 * 
	 * @param name	Name of the philosoph who wants to give back the seat.
	 */
	public void giveBack(String name) throws RemoteException;
	
	/**
	 * Waiting until seat is empty.
	 */
	public void waitForEmptySeat() throws RemoteException;
	
	/**
	 * Get name of seat in the registry
	 * 
	 * @return	Name of seat in the registry.
	 */
	public String getSeatName() throws RemoteException;
}
