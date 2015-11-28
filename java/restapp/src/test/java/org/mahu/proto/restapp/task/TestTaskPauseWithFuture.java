package org.mahu.proto.restapp.task;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mahu.proto.restapp.engine.impl.ProcessTaskFutureImpl;
import org.mahu.proto.restapp.model.Node;
import org.mahu.proto.restapp.model.PausableTask;
import org.mahu.proto.restapp.model.ProcessTask;

public class TestTaskPauseWithFuture implements PausableTask, ProcessTask {
	
	final static Logger logger = LogManager.getLogger(TestTaskPauseWithFuture.class.getName());	

	@Inject
	Node node;

	// Note that the used Inject here uses an internal impl class (to make test easy for now)
	// normal there is no Named and just: private ProcessTaskFuture future;
	@Inject
	@Named("org.mahu.proto.restapp.engine.ProcessTaskFuture")
	private ProcessTaskFutureImpl future;

	@Override
	public Enum<?> execute() throws ProcessTaskException {
		logger.info("TestTaskPauseWithFuture.execute() future.isDone="+future.IsDone());
		if (future.IsDone()) {
			return ProcessTask.Result.Next;
		} else {
			(new AsyncTask()).start();
			return PausableTask.Result.Pauze;
		}
	}

	@Override
	public void cancel() {
	}

	class AsyncTask extends Thread {
		public void run() {
			logger.info("TestTaskPauseWithFuture.AsyncTask.run() ENTER");
			try {
				Thread.sleep(1000/* MS */);
				future.AsyncTaskHasCompleted();
			} catch (InterruptedException e) {
				// ignore
			} finally {
				logger.info("TestTaskPauseWithFuture.AsyncTask.run() LEAVE");
			}
		}
	}
}