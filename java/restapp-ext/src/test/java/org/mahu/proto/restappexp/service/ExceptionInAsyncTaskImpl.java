package org.mahu.proto.restappexp.service;

import java.util.concurrent.Callable;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mahu.proto.restapp.asyntask.AsyncTaskManager;
import org.mahu.proto.restapp.engine.ProcessTaskFuture;

public class ExceptionInAsyncTaskImpl implements ExceptionInAsyncTaskInterface {

	final static Logger logger = LogManager.getLogger(ExceptionInAsyncTaskImpl.class.getName());

	@Inject
	private AsyncTaskManager asyncTaskManager;

	@Inject
	private ProcessTaskFuture future;

	static class AsyncTask implements Callable<Void> {
		private final String name;

		AsyncTask(final String name) {
			this.name = name;
		}

		@Override
		public Void call() {
			logger.debug(name + " call() ENTER ### THROWING AN EXCEPTION");
			throw new RuntimeException("Breaking the waves");
		}
	}

	@Override
	public void StartAsyncTask() {
		logger.info("StartAsyncTask() ENTER");
		asyncTaskManager.Submit(future.GetSessionId(), new AsyncTask(
				"AsyncTask"));
		logger.info("StartAsyncTask() LEAVE");
	}
}
