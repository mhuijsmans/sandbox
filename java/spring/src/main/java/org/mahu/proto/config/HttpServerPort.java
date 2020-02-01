package org.mahu.proto.config;

import org.springframework.core.env.Environment;

final class HttpServerPort implements IHttpServerPort {

	private final Environment environment;

	HttpServerPort(final Environment environment) {
		this.environment = environment;
	}

	@Override
	public String getListeningPort() {
		// The port may not be present when constructor is called, because Embedded
		// tomcat may be started later than when this object is created.
		final String port = environment.getProperty("local.server.port");
		return port;
	}

}
