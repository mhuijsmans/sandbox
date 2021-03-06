package org.mahu.proto.restappextra.remoteservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/hello")
public class IRESTService {

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response get() {
		System.out.println(IRESTService.class.getName()+".get()");
		return Response.ok("hello").build();
	}

}
