package org.mahu.proto.webresttest;

import java.util.logging.Logger;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("rest")
public class RestMainServlet extends ResourceConfig {
	protected static final Logger LOGGER = Logger.getLogger(RestMainServlet.class
			.getName());	
	
	public RestMainServlet() {
		LOGGER.info("This is good news");
		register(EmployeeController.class);
	}
}