package vss3.aufgabe5;

import org.apache.log4j.Logger;

import vss3.aufgabe5.client.SalesmenWorker;

/**
 * Starts as many Clients as cores are available on the client system.
 */
public class SalesmenClientMain {
	
	/**
	 * Logger
	 */
	private static final Logger LOGGER = Logger.getLogger(SalesmenClientMain.class);
	
	/**
	 * Starts one worker for each logical core on the PC.
	 * 
	 * @param args	The IP-address of the server.
	 */
	public static void main(String[] args){
		int cores = Runtime.getRuntime().availableProcessors();
		LOGGER.info("Started "+cores+" Worker.");
		for(int i = 0; i<cores; i++){
			new SalesmenWorker(args[0]);
		}
	}
}
