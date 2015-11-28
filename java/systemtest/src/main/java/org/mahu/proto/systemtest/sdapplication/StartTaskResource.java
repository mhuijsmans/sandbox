package org.mahu.proto.systemtest.sdapplication;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.mahu.proto.systemtest.CommonConst;

@Path("/" + CommonConst.SD_CONTEXT)
public class StartTaskResource {

	private SDController controller;

	@Inject
	public void setHandler(final SDController controller) {
		this.controller = controller;
	}

	@GET
	@Produces("text/plain")
	public String start() {
		controller.start();
		return "Work Completed";
	}

}