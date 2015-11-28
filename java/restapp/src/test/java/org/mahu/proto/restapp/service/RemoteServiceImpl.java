package org.mahu.proto.restapp.service;

import java.util.concurrent.Callable;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mahu.proto.restapp.asyntask.AsyncTaskManager;
import org.mahu.proto.restapp.engine.ProcessTaskFuture;

public class RemoteServiceImpl implements RemoteService {

	final static Logger logger = LogManager.getLogger(RemoteServiceImpl.class.getName());

	@Inject
	private AsyncTaskManager asyncTaskManager;

	@Inject
	private ProcessTaskFuture future;	

	@Override
	public void Call() {
		logger.info("Call() ENTER");
		asyncTaskManager.Submit(future.GetSessionId(), new AsyncTask("AsyncRSTask", future));
		logger.info("Call() LEAVE");
	}

	static class AsyncTask implements Callable<Void> {
		private final ProcessTaskFuture future;
		private final String name;

		AsyncTask(final String name, final ProcessTaskFuture future) {
			this.future = future;
			this.name = name;
		}

		@Override
		public Void call() {
			logger.debug("ENTER " + name);
			future.AsyncTaskHasCompleted();			
			logger.debug("LEAVE " + name);
			return null;
		}
	}

}
