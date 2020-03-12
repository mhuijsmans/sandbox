package org.mahu.tools.syslogservice;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


// netcat can provide same functionality as all this code:
// https://www.digitalocean.com/community/tutorials/how-to-use-netcat-to-establish-and-test-tcp-and-udp-connections-on-a-vps
public class Main {
	
	private static final int DEFAULT_LISTENING_PORT = 20000;

	public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
		final int listeningPort = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_LISTENING_PORT;
		
		Logger.log("Starting syslog service on localhost:" + listeningPort);

		try (SysLogServerConsole logServerConsole = new SysLogServerConsole(listeningPort)) {
			Future<?> future = logServerConsole.start();
			future.get();
		} finally {
			Logger.log("Syslog service has terminated");
		}
	}
}
