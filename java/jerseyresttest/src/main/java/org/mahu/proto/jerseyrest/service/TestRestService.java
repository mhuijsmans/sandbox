package org.mahu.proto.jerseyrest.service;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/test")
public class TestRestService {

	private final static Logger LOGGER = Logger.getLogger(TestRestService.class
			.getName());

	public static interface TestRestServiceHandler {
		public void doAction();
	}

	public static class IShutdownServiceHandlerImpl implements
			TestRestServiceHandler {

		@Override
		public void doAction() {
			LOGGER.info("shutdown, calling System.exit(0)");
		}

	}

	private TestRestServiceHandler handler = new IShutdownServiceHandlerImpl();

	@Inject
	public void setHandler(final TestRestServiceHandler aHandler) {
		handler = aHandler;
	}

	@GET
	@Path("/info")
	@Produces(MediaType.TEXT_XML)
	public TestInfo info() {
		return new TestInfo();
	}

	@POST
	@Path("/action1")
	@Produces(MediaType.TEXT_XML)
	public Action1Response shutdown2() {
		LOGGER.info("POST shutdown");
		handler.doAction();
		return new Action1Response();
	}

}
