package org.mahu.proto.restappextra.remoteservice;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mahu.proto.embeddedjetty.RESTServer;
import org.mahu.proto.restappextra.TestConst;

public class FServiceSystem1 {

	final static Logger logger = LogManager.getLogger(FServiceSystem1.class.getName());

	// EntryPoint for the process that provides the
	public static void main(String[] args) {
		System.out.println(FServiceSystem1.class + " process started  at port="+TestConst.SYSTEM1_FPORT);
		RESTServer server = new RESTServer();
		try {
			server.start(TestConst.SYSTEM1_FPORT, FRESTService.class.getName());
			new Thread(new DetectedParentGone(server)).start();
			MeetupClient.tellImPresent();
			server.join();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println(FServiceSystem1.class + " process terminated");
		}
		System.exit(0);
	}

	static class DetectedParentGone implements Runnable {
		private final RESTServer server;

		DetectedParentGone(final RESTServer server) {
			this.server = server;
		}
		@Override
		public void run() {
			System.out.println(FServiceSystem1.class + " DetectedParentGone started");
			try {
				// wait for stdin to close.
				while (System.in.read()>=0) {
					// do nothing
				}
			} catch (IOException e) {
				try {
					server.stop();
				} catch (Exception e1) {
				}
			} finally {
				System.out.println(FServiceSystem1.class + " DetectedParentGone terminated");
			}
		}

	}

}
