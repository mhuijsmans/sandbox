package org.mahu.proto.restappextra.service;

import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mahu.proto.jerseytools.RestResource;
import org.mahu.proto.jerseytools.RestResourceUri;
import org.mahu.proto.restapp.asyntask.AsyncTaskManager;
import org.mahu.proto.restapp.engine.ProcessTaskFuture;
import org.mahu.proto.restappextra.config.SystemConfiguration;

public class VProxy implements VInterface {

	final static Logger logger = LogManager.getLogger(VProxy.class);

	@Inject
	private AsyncTaskManager asyncTaskManager;

	@Inject
	private ProcessTaskFuture future;

	@Inject
	private SystemConfiguration systemConfiguration;

	static class AsyncTask implements Callable<Void> {
		private final ProcessTaskFuture future;
		private final String name;
		private final String baseUrl;
		private final String function;

		AsyncTask(final String name, final String baseUrl,
				final String function, final ProcessTaskFuture future) {
			this.future = future;
			this.name = name;
			this.baseUrl = baseUrl;
			this.function = function;
		}

		@Override
		public Void call() {
			logger.debug(name + " call() ENTER()");
			RestResourceUri uri = new RestResourceUri(baseUrl, "v/" + function);
			RestResource<String> resource = new RestResource<String>(uri,
					String.class);
			logger.info(name + " sending http request, uri=" + uri);
			resource.setMediaType(MediaType.TEXT_PLAIN);
			//
			resource.doGet();
			logger.info(name + " http.response.status="
					+ resource.getResponseCode());
			logger.debug(name + " http.response.body=" + resource.getData());
			if (resource.getResponseCode() == 200) {
				future.AsyncTaskHasCompleted();
				logger.debug(name + " call() LEAVE");
			} else {
				// TODO: exception handling for future
				logger.debug(name + " call() LEAVE throwing exception");
				throw new RuntimeException("Call failed name=" + name);
			}
			return null;
		}
	}

	@Override
	public void DoLD() {
		logger.info("DoLD() ENTER");
		asyncTaskManager
				.Submit(future.GetSessionId(), new AsyncTask("AsyncLDTask",
						systemConfiguration.GetVBaseUrl(), "ld", future));
		logger.info("DoLD() LEAVE");
	}

	@Override
	public void DoBC() {
		logger.info("DoBC() ENTER");
		asyncTaskManager
				.Submit(future.GetSessionId(), new AsyncTask("AsyncBCTask",
						systemConfiguration.GetVBaseUrl(), "bc", future));
		logger.info("DoBC() LEAVE");
	}

	@Override
	public void DoED() {
		logger.info("DoED() ENTER");
		asyncTaskManager
				.Submit(future.GetSessionId(), new AsyncTask("AsyncEDTask",
						systemConfiguration.GetVBaseUrl(), "ed", future));
		logger.info("DoED() LEAVE");
	}

	@Override
	public void DoSED() {
		logger.info("DoSED() ENTER");
		asyncTaskManager.Submit(future.GetSessionId(),
				new AsyncTask("AsyncSETask", systemConfiguration.GetVBaseUrl(),
						"sed", future));
		logger.info("DoSED() LEAVE");
	}

}
