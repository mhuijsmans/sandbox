package org.mahu.proto.embeddedtomcat;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/hello")
public class TESTService {
	
	public final static String RESPONSE = "hello";

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response get() {
		System.out.println("TESTService.get()");
		return Response.ok(RESPONSE).build();
	}

}
