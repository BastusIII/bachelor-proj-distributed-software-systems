package vss3.aufgabe4;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IController extends Remote{

	/**
	 * Count the eating of the philosophers.
	 * 
	 * @param philosopherId		The id of the philosopher
	 * @param philosopherName	The name of the philosopher
	 * @throws RemoteException	If the controller is not reachable.
	 */
	public void philosopherStartsEating(int philosopherId, String philosopherName) throws RemoteException;
}
