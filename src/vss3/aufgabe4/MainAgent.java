package vss3.aufgabe4;

import org.apache.log4j.Logger;

/**
 * Main 
 * starts a new Agent
 * 
 * @author Waldleitner
 *
 */
public class MainAgent {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Logger log = Logger.getLogger(MainAgent.class);
		if (args.length>0) {
			String[] ipPort = args[0].split(":");
			if (ipPort.length >= 2) {
				new Agent(ipPort[0], Integer.parseInt(ipPort[1]));
			} else {
				log.error("Missing start parameters!");
			}
		} else {
			log.error("Missing all start parameters!");
		}
	}
}
