package vss3.aufgabe4;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

public interface IAgent extends Remote{
	
	/**
	 * get the controller
	 * 
	 * @return the controler
	 */
	public IController getController() throws RemoteException;

	/**
	 * creates a philosopher in this agent
	 * 
	 * @param id	Id of the new philosopher
	 * @param hungry	is the philosopher actually hungry?
	 * @param maxOccupiedTime	time the philosopher waits between eating
	 * @return	name of the philosopher in the registry
	 */
	public String createPhilosopher(final int id, final boolean hungry, final int maxOccupiedTime) throws RemoteException;
	
	/**
	 * removes the philosopher
	 * 
	 * @param id Id of the philosopher
	 */
	public void removePhilosopher(int id) throws RemoteException;
	
	/**
	 * creates a new fork in this agent
	 * 
	 * @param id	Id of the new Fork
	 * @return		name of the fork in the registry
	 */
	public String createFork(int id) throws RemoteException;
	
	/**
	 * removes a fork
	 * 
	 * @param id	Id of the fork
	 */
	public void removeFork(int id) throws RemoteException;
	
	/**
	 * creates a new seat in this agent
	 * 
	 * @param id	Id of the new seat
	 * @return		name of the seat in the registry
	 */
	public String createSeat(int id) throws RemoteException;
	
	/**
	 * removes a seat
	 * 
	 * @param id	Id of the seat
	 */
	public void removeSeat(int id) throws RemoteException;
	
	/**
	 * set a new list of seats
	 * 
	 * @param seatList	Array of seats
	 */
	public void setSeatList(String[] seatList) throws RemoteException;
	/**
	 * set a new list of forks
	 * 
	 * @param forkList	Array of forks
	 */
	public void setForkList(String[] forkList) throws RemoteException;
	
	/**
	 * Get the list of all registered seats
	 * 
	 * @return		list of all registered seats
	 * @throws RemoteException	if agent not reachable.
	 */
	public String[] getSeatList() throws RemoteException;
	/**
	 * Get the list of all registered forks
	 * 
	 * @return		list of all registered forks
	 * @throws RemoteException	if agent not reachable.
	 */
	public String[] getForkList() throws RemoteException;
	/**
	 * Get a fork(local, remote)
	 * 
	 * @param name		Name of the fork
	 * @return			local or remote fork
	 * @throws RemoteException	if agent not reachable
	 */
	public IFork getFork(String name) throws RemoteException;
	/**
	 * Get a seat(local, remote)
	 * 
	 * @param name		Name of the seat
	 * @return			local or remote seat
	 * @throws RemoteException	if agent not reachable
	 */
	public ISeat getSeat(String name) throws RemoteException;
	/**
	 * Is the Agent still connected?
	 * 
	 * @return	true is still connected
	 */
	public boolean ping() throws RemoteException;
}
