package org.mahu.proto.startup;

import org.mahu.proto.config.IHttpServerPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ApplicationRestReachableCheck implements IApplicationRestReachableCheck {

	private static final Logger LOG = LoggerFactory.getLogger(ApplicationRestReachableCheck.class);

	private final IHttpServerPort httpServerPort;

	@Autowired	
	ApplicationRestReachableCheck(final IHttpServerPort httpServerPort) {
		this.httpServerPort = httpServerPort;
	}

	public void performReachabilityCheck() {
		LOG.info("ApplicationRestReachableCheck.executeStartupLogic() - enter");

		RestTemplate restTemplate = new RestTemplate();
		String resourceUrl = "http://localhost:" + httpServerPort.getListeningPort() + "/rest/state";

		RetryTemplate retryTemplate = createRetryTemplate();
		retryTemplate.execute(arg0 -> {
			ResponseEntity<String> response = restTemplate.getForEntity(resourceUrl, String.class);
			LOG.info("ApplicationRestReachableCheck.executeStartupLogic() - response=" + response.getStatusCodeValue());
			return null;
		});

		LOG.info("ApplicationRestReachableCheck.executeStartupLogic() - leave");
	}

	private RetryTemplate createRetryTemplate() {
		final int maxAttempt = 5;
		final int retryTimeInterval = 1500;

		SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
		retryPolicy.setMaxAttempts(maxAttempt);
		FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
		backOffPolicy.setBackOffPeriod(retryTimeInterval); // 1.5 seconds

		RetryTemplate retryTemplate = new RetryTemplate();
		retryTemplate.setRetryPolicy(retryPolicy);
		retryTemplate.setBackOffPolicy(backOffPolicy);

		return retryTemplate;
	}

}
