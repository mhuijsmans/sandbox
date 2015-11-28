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

public class SProxy implements SInterface {

	final static Logger logger = LogManager.getLogger(SProxy.class.getName());

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

		AsyncTask(final String name, final String baseUrl, final String function, 
				final ProcessTaskFuture future) {
			this.future = future;
			this.name = name;
			this.baseUrl = baseUrl;
			this.function = function;
		}

		@Override
		public Void call() {
			logger.debug(name + " call() ENTER");
			RestResourceUri uri = new RestResourceUri(baseUrl, "s/"+function);
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
	public void CheckC() {
		logger.info("CheckC() ENTER");
		asyncTaskManager.Submit(future.GetSessionId(), new AsyncTask(
				"AsyncCTask", systemConfiguration.GetSBaseUrl(), "s", future));
		logger.info("CheckC() LEAVE");
	}

}
