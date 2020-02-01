package org.mahu.proto.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
// Note: can not be final, because Spring will complain.
public class HttpServerConfiguration {

	private final Environment environment;

	@Autowired
	HttpServerConfiguration(final Environment environment) {
		this.environment = environment;
	}

	@Bean
	IHttpServerPort getHttpServerProperties() {
		// At this moment the environment does not contain the port, so pass environment. 
		return new HttpServerPort(environment);
	}

}
