package org.mahu.proto.jerseyjunittools;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;

/**
 * This class holds the base URI for a set of RestResources. By addressing
 * resources via the class, the connection towards the server is re-used. This
 * reduces latency. Open: can the the connection be used by several resources in
 * parallel.
 */
public class RestResourceBase {

	private final URI baseUrl;
	private final WebTarget target;

	public RestResourceBase(final URI aBaseUrl) {
		baseUrl = aBaseUrl;
		Client client = ClientBuilder.newClient();
		target = client.target(baseUrl);
	}

	public RestResourceBase(final URI aBaseUrl, final int port) {
		baseUrl = UriBuilder.fromUri("http://" + aBaseUrl.getHost())
				.port(aBaseUrl.getPort() + port).build();
		Client client = ClientBuilder.newClient();
		target = client.target(baseUrl);
	}

	public WebTarget target(final String resourcePath) {
		return target.path(resourcePath);
	}
}
