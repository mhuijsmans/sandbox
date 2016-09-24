package org.mahu.multiapp.app2;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/app2")
public class HelloWorldService1 {

	@GET
    @Path("/hello1")
	public Response hello() {
        String output = "App2 says hello1";
 		return Response.status(200).entity(output).build();
	}
}