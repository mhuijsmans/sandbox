package org.mahu.proto.restapp.task;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mahu.proto.restapp.engine.ProcessTaskFuture;
import org.mahu.proto.restapp.model.Node;
import org.mahu.proto.restapp.model.PausableTask;
import org.mahu.proto.restapp.model.ProcessTask;
import org.mahu.proto.restapp.service.RemoteService;

public class TestTaskPauseWithFuture2 implements PausableTask, ProcessTask {

	final static Logger logger = LogManager.getLogger(TestTaskPauseWithFuture2.class.getName());

	@Inject
	Node node;

	@Inject
	RemoteService remoteService;

	@Inject
	ProcessTaskFuture future;

	@Override
	public Enum<?> execute() throws ProcessTaskException {
		boolean isDone = future.IsDone();
		logger.debug("LDTask: node=" + node.getName()+" future.isDone="+isDone);
		if (isDone == false) {
			remoteService.Call();
			return PausableTask.Result.Pauze;
		} else {
			return ProcessTask.Result.Next;
		}
	}

	@Override
	public void cancel() {
		
	}

}