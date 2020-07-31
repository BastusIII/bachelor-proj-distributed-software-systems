package vss3.aufgabe4;

import java.rmi.Remote;

/**
 * Information container for remote objects on foreign agents.
 */
public class RemoteInformation {

	/**
	 * The name of the remote that is registered in the registry.
	 */
	private String name;

	/**
	 * The name of the agent, where this remote is running on.
	 */
	private String agentName;

	/**
	 * The number of the remote.
	 */
	private int number;

	/**
	 * The class of the remote.
	 */
	private Class<? extends Remote> remoteClass;

	public RemoteInformation(Class<? extends Remote> clazz, final String name, final String agentName, final int number) {
		this.remoteClass = clazz;
		this.name = name;
		this.agentName = agentName;
		this.number = number;
	}

	/**
	 * Returns the name, under which the remote is registered.
	 * @return The name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the name of the agent, where this remote s running on.
	 * @return The name of the remotes agent.
	 */
	public String getAgentName() {
		return agentName;
	}

	/**
	 * Returns the number of the remote.
	 * @return The number.
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * The class of the remote.
	 * @return the class.
	 */
	public Class<? extends Remote> getRemoteClass() {
		return remoteClass;
	}

	/**
	 * Sets the name.
	 * @param name The new name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the agent name.
	 * @param agentName The new agent name.
	 */
	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}
}
