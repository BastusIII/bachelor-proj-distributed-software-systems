package vss3.aufgabe4;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IFork extends Remote{
	
	/**
	 * get Id of the fork
	 * 
	 * @return	Id of the fork
	 */
	public int getId() throws RemoteException;
	
	/**
	 * Take a fork
	 * 
	 * @param name	Name of the Philisoph who wants to take the fork
	 */
	public void take(String name) throws RemoteException;
	
	/**
	 * Give back the fork
	 * 
	 * @param name	Name of the Philisoph who wants to give back the fork
	 */
	public void giveBack(String name) throws RemoteException;
	
	/**
	 * Get name of fork
	 * 
	 * @return	Name of fork in the registry.
	 */
	public String getForkName() throws RemoteException;
}
