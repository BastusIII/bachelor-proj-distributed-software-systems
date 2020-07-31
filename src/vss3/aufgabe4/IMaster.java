package vss3.aufgabe4;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IMaster extends Remote {
	/**
	 * Bind an remote object to the registry
	 * 
	 * @param remote	The remote object to bind to the registry
	 * @param name		The name to the remote object
	 * @throws RemoteException	if master is not reachable.
	 */
    public <T extends Remote> void bindMe(T remote, String name) throws RemoteException;

    /**
     * Unbind an remote object from the registry
     * 
     * @param name	The name of the remote object
     * @throws RemoteException	if master is not reachable
     */
    public void unbindMe(String name) throws RemoteException;
}
