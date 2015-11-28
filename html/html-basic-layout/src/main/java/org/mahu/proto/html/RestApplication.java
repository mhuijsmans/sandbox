package org.mahu.proto.html;

import java.util.logging.Logger;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;
import org.mahu.proto.html.rest.ActionService;

@ApplicationPath("restcall")
public class RestApplication extends ResourceConfig {
	protected static final Logger LOGGER = Logger.getLogger(RestApplication.class
			.getName());	
	
	public RestApplication() {
		register(ActionService.class);
	}
}
