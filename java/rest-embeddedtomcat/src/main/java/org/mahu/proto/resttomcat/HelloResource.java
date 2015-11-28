package org.mahu.proto.resttomcat;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
 
@Path("hello")
public class HelloResource {
 
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public Response get() {
    return Response.ok("hello").build();
  }
}
