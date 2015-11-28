package org.mahu.proto.restappextra;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mahu.proto.embeddedjetty.RESTServer;
import org.mahu.proto.restappextra.remoteservice.VServiceSystem1;

/**
 * Meetup class. Only one instance of class can be run.
 */
public class MeetupRestService {

	final static Logger logger = LogManager.getLogger(VServiceSystem1.class
			.getName());

	private final int NrOfParticipants;
	private RESTServer server;

	MeetupRestService(final int NrOfParticipants) {
		this.NrOfParticipants = NrOfParticipants;
	}

	public void Start() {
		logger.info("Start ENTER");
		server = new RESTServer();
		try {
			MeetupService.cdl = new CountDownLatch(NrOfParticipants);
			server.start(TestConst.MEETUP_PORT, MeetupService.class.getName());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		logger.info("Start LEAVE");
	}

	public void WaitForAllParticipants(final int MaxWaitInSeconds) {
		logger.info("WaitForAllParticipants ENTER");
		try {
			MeetupService.cdl.await(MaxWaitInSeconds, TimeUnit.SECONDS);
			// Sleep is needed so that response can be send.
			Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				server.stop();
			} catch (Exception e) {
				logger.error("WaitForAllParticipants", e);
			}
		}
		if (MeetupService.cdl.getCount()!=0) {
			throw new RuntimeException("Not all participants joined. Missing="+MeetupService.cdl.getCount());
		}
		logger.info("WaitForAllParticipants LEAVE");
	}

	@Path("/noknok")
	static public class MeetupService {

		private static CountDownLatch cdl = null;

		@GET
		@Produces(MediaType.TEXT_PLAIN)
		public Response get() {
			logger.info(MeetupService.class.getName() + ".get()");
			if (cdl != null) {
				cdl.countDown();
			}
			return Response.ok("hello").build();
		}

	}

}
