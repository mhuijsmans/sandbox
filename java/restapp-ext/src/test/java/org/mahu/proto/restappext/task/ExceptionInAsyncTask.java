package org.mahu.proto.restappext.task;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mahu.proto.restapp.engine.ProcessTaskFuture;
import org.mahu.proto.restapp.model.Node;
import org.mahu.proto.restapp.model.PausableTask;
import org.mahu.proto.restapp.model.ProcessTask;
import org.mahu.proto.restappexp.service.ExceptionInAsyncTaskInterface;

public class ExceptionInAsyncTask implements ProcessTask, PausableTask {

	final static Logger logger = LogManager.getLogger(ExceptionInAsyncTask.class.getName());

	@Inject
	Node node = null;
	
	@Inject
	ExceptionInAsyncTaskInterface exceptionInAsyncTaskInterface;
	
	@Inject
	ProcessTaskFuture future;	

	@Override
	public Enum<?> execute() throws ProcessTaskException {
		logger.debug("ExceptionInAsyncTask ENTER");
		boolean isDone = future.IsDone();
		logger.debug("LDTask: node=" + node.getName()+" future.isDone="+isDone);
		if (isDone == false) {
			exceptionInAsyncTaskInterface.StartAsyncTask();
			logger.debug("ExceptionInAsyncTask LEAVE");
			return PausableTask.Result.Pauze;
		} else {
			logger.debug("ExceptionInAsyncTask LEAVE");
			return ProcessTask.Result.Next;
		}		
	}

	@Override
	public void cancel() {
	}

}