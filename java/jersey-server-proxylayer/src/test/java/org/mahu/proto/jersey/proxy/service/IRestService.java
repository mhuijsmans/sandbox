package org.mahu.proto.jersey.proxy.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/test")
public interface IRestService {

	@GET
	@Path("/info")
	@Produces(MediaType.TEXT_PLAIN)
	public String getInfo();
	
	@GET
	@Path("/module-info")
	@Produces(MediaType.TEXT_PLAIN)
	public String getModuleInfo();	
	
	@POST
	@Path("/more-info")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String getMoreInfo(final String input);	
	
	@GET
	@Path("/request-scoped-info")
	@Produces(MediaType.TEXT_PLAIN)
	public String getRequestSCopedInfo();		
	
	@GET
	@Path("/truth")
	@Produces(MediaType.TEXT_PLAIN)
	public String getTruth();	

}
