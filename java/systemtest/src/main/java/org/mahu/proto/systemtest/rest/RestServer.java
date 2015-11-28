package org.mahu.proto.systemtest.rest;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

public class RestServer {

	private static final Logger LOGGER = Logger.getLogger(RestServer.class
			.getName());

	private final URI baseUri;
	private HttpServer server;
	private final ResourceConfig resourceConfig = new ResourceConfig();

	public RestServer(final URI baseUri) {
		this.baseUri = baseUri;
		//
		resourceConfig
				.register(org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpContainerProvider.class);
		resourceConfig.property(
				ServerProperties.METAINF_SERVICES_LOOKUP_DISABLE, true);
		//
		if (LOGGER.isLoggable(Level.FINE)) {
			resourceConfig.register(new LoggingFilter(LOGGER, true));
			resourceConfig.property(ServerProperties.TRACING, "ALL");
		}
	}

	public ResourceConfig getResourceConfig() {
		return resourceConfig;
	}

	public void startServer() {
		server = GrizzlyHttpServerFactory.createHttpServer(baseUri,
				resourceConfig);
	}

	public void stopServer() {
		if (server != null) {
			server.shutdownNow();
		}
	}
}