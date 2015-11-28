package org.mahu.proto.systemtest.adapplication;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.mahu.proto.systemtest.CommonConst;

@Path("/" + CommonConst.AD_CONTEXT)
public class StartTaskResource {

	 private ADController controller;
	
	 @Inject
	 public void setHandler(final ADController controller) {
		 this.controller = controller;
	 }

	@GET
	@Produces("text/plain")
	public String start() {
		controller.start();
		return "Work Completed";
	}

}