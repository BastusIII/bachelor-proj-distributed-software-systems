package vss3.aufgabe4;

import sun.rmi.registry.RegistryImpl;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;

/**
 * Registry that binds known remotes to the master.
 */
public class PhilosopherRegistry extends RegistryImpl {

	private Master myMaster;

	public PhilosopherRegistry(int i, Master master) throws RemoteException {
		super(i);
		myMaster = master;
	}

	@Override
	public void rebind(java.lang.String s, java.rmi.Remote remote) throws java.rmi.RemoteException {
		register(s, remote);
		super.rebind(s, remote);
	}

	@Override
	public void bind(java.lang.String s, java.rmi.Remote remote) throws java.rmi.RemoteException, AlreadyBoundException {
		register(s, remote);
		super.bind(s, remote);
	}

	/**
	 * Registers the remote object at the master.
	 * @param s Name that the remote is bound to.
	 * @param remote The remote bound.
	 */
	private void register(java.lang.String s, java.rmi.Remote remote) {
		if (remote instanceof IAgent) {
			myMaster.register(s, (IAgent)remote);
		}
		if (remote instanceof IController) {
			myMaster.register(s);
		}
	}

}
