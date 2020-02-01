package org.mahu.proto.startup;

import java.util.concurrent.Future;

import org.mahu.proto.config.IHttpServerPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ApplicationInitializationRequest implements IApplicationInitializationRequest {

	private static final Logger LOG = LoggerFactory.getLogger(ApplicationRestReachableCheck.class);

	private final IHttpServerPort httpServerPort;

	@Autowired
	ApplicationInitializationRequest(final IHttpServerPort httpServerPort) {
		this.httpServerPort = httpServerPort;
	}

	@Override
	@Async
	public Future<Boolean> performInitialization() {
		LOG.info("ApplicationInitializationRequest.performInitialization() - enter");

		RestTemplate restTemplate = new RestTemplate();
		String resourceUrl = "http://localhost:" + httpServerPort.getListeningPort() + "/rest/startup";

		ResponseEntity<String> response = restTemplate.postForEntity(resourceUrl,"", String.class);
		LOG.info("ApplicationInitializationRequest.performInitialization() - response=" + response.getStatusCodeValue());

		LOG.info("ApplicationInitializationRequest.performInitialization() - leave");
		
		return new AsyncResult<>(true);
	}

}
