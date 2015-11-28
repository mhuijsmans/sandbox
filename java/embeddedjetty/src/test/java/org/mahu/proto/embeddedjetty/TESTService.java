package org.mahu.proto.embeddedjetty;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/hello")
public class TESTService {
	
	public final static String RESPONSE1 = "hello";
	public final static String RESPONSE2 = "hellohello";

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response get() {
		System.out.println("TESTService.get1()");
		return Response.ok(RESPONSE1).build();
	}
	
		
	@GET
	@Path("/hello")
	@Produces(MediaType.TEXT_PLAIN)
	public Response get1() {
		System.out.println("TESTService.get2()");
		return Response.ok(RESPONSE2).build();
	}

}
