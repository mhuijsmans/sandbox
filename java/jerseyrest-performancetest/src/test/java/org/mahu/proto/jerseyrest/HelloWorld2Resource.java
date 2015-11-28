package org.mahu.proto.jerseyrest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/helloworld")
// Note that class must be public
public class HelloWorld2Resource {
	@GET
	@Path("images/{imageid}")
	@Produces({ "image/png" })
	public Response retrieveGifImage(@PathParam("imageid") String imageId) {
		// System.out.println("Serving " + imageId);
		final int length = 23000000;
		Response resp = RestUtils.writeResourceToResponse(length);
		// resp..setHeader ( "Content-Length", length );
		return resp;
	}
}