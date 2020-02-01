package org.mahu.proto.startup;

import java.util.concurrent.Future;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class Startup {

	private static final Logger LOG = LoggerFactory.getLogger(Startup.class);

	private final Object lock = new Object();
	private Future<Boolean> initialisationRequest;

	@Autowired
	private IApplicationRestReachableCheck applicationRestReachableCheck;
	@Autowired
	private IApplicationInitializationRequest applicationInitializationRequest;

	// ApplicationReadyEvent: Event published as late as conceivably possible to
	// indicate
	// that the application is ready to service requests
	@EventListener(ApplicationReadyEvent.class)
	public void executeStartupLogic() {
		LOG.info("Startup.executeStartupLogic() - enter");

		applicationRestReachableCheck.performReachabilityCheck();

		synchronized (lock) {
			initialisationRequest = applicationInitializationRequest.performInitialization();
		}

		LOG.info("Startup.executeStartupLogic() - leave");
	}

	@PreDestroy
	public void terminateInitializationRequest() {
		if (!initialisationRequest.isDone()) {
			final boolean mayInterruptIfRunning = true;
			initialisationRequest.cancel(mayInterruptIfRunning);
		}

	}

}
