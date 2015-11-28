package org.mahu.proto.restappextra.remoteservice;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/s")
public class SRESTService {

	@POST
	@Path("/bl")
	@Produces(MediaType.TEXT_PLAIN)
	public Response postbl() {
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		System.out.println("### "+SRESTService.class.getName() + "." + name+" ###");
		return Response.ok("hello").build();
	}

	@GET
	@Path("/bl")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getbl() {
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		System.out.println("### "+SRESTService.class.getName() + "." + name+" ###");
		return Response.ok("hello").build();
	}

	@POST
	@Path("/tl")
	@Produces(MediaType.TEXT_PLAIN)
	public Response posttl() {
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		System.out.println("### "+SRESTService.class.getName() + "." + name+" ###");
		return Response.ok("hello").build();
	}

	@GET
	@Path("/tl")
	@Produces(MediaType.TEXT_PLAIN)
	public Response gettl() {
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		System.out.println("### "+SRESTService.class.getName() + "." + name+" ###");
		return Response.ok("hello").build();
	}

	@POST
	@Path("/msk")
	@Produces(MediaType.TEXT_PLAIN)
	public Response postmsk() {
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		System.out.println("### "+SRESTService.class.getName() + "." + name+" ###");
		return Response.ok("hello").build();
	}

	@GET
	@Path("/rl")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getrl() {
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		System.out.println("### "+SRESTService.class.getName() + "." + name+" ###");
		return Response.ok("hello").build();
	}

	@POST
	@Path("/rspl")
	@Produces(MediaType.TEXT_PLAIN)
	public Response postrspl() {
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		System.out.println("### "+SRESTService.class.getName() + "." + name+" ###");
		return Response.ok("hello").build();
	}

	@PUT
	@Path("/pf")
	@Produces(MediaType.TEXT_PLAIN)
	public Response putf() {
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		System.out.println("### "+SRESTService.class.getName() + "." + name+" ###");
		return Response.ok("hello").build();
	}
	
	@GET
	@Path("/t")
	@Produces(MediaType.TEXT_PLAIN)
	public Response gett() {
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		System.out.println("### "+SRESTService.class.getName() + "." + name+" ###");
		return Response.ok("hello").build();
	}
		
	@GET
	@Path("/ts")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getts() {
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		System.out.println("### "+SRESTService.class.getName() + "." + name+" ###");
		return Response.ok("hello").build();
	}
	
	@GET
	@Path("/s")
	@Produces(MediaType.TEXT_PLAIN)
	public Response gets() {
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		System.out.println("### "+SRESTService.class.getName() + "." + name+" ###");
		return Response.ok("hello").build();
	}	
}
