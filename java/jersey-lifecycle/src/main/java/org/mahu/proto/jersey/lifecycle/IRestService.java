package org.mahu.proto.jersey.lifecycle;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/test")
public interface IRestService {

	@GET
	@Path("/info1")
	@Produces(MediaType.TEXT_PLAIN)
	public String getInfo1();
	
	@GET
	@Path("/info2")
	@Produces(MediaType.TEXT_PLAIN)
	public String getInfo2();	


}
