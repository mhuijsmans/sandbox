package org.mahu.proto.jersey.lifecycle;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/test")
public interface IRestartRestService {

	@POST
	@Path("/restart")
	@Produces(MediaType.TEXT_PLAIN)
	public String restart();

}
