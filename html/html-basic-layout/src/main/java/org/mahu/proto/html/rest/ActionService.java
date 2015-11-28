package org.mahu.proto.html.rest;

import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/action")
public class ActionService {

	protected static final Logger LOGGER = Logger.getLogger(ActionService.class
			.getName());

	@GET
	@Produces(MediaType.TEXT_XML)
	@Path("info")
	public ActionInfo status() {
		LOGGER.info("GET action/Info");
		return createActionInfo();
	}
	
	@POST
	@Produces(MediaType.TEXT_XML)
	@Path("start")
	public ActionInfo start() {
		LOGGER.info("POST Action/Info");
		return createActionInfo();
	}	
	
	protected ActionInfo createActionInfo() {
		ActionInfo ai = new ActionInfo();
		ai.setState(ActionMan.isBusy() ? ActionInfo.STATE_BUSY : ActionInfo.STATE_IDLE);
		return ai;
	}

}
