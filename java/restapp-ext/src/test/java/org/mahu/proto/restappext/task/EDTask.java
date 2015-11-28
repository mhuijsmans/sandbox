package org.mahu.proto.restappext.task;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mahu.proto.restapp.engine.ProcessTaskFuture;
import org.mahu.proto.restapp.model.Node;
import org.mahu.proto.restapp.model.PausableTask;
import org.mahu.proto.restapp.model.ProcessTask;
import org.mahu.proto.restappexp.service.VEInterface;

public class EDTask implements ProcessTask, PausableTask {

	final static Logger logger = LogManager.getLogger(EDTask.class.getName());

	@Inject
	Node node;

	@Inject
	VEInterface ve;

	// Future could be moved to PausableTask implementation
	@Inject
	ProcessTaskFuture future;

	@Override
	public Enum<?> execute() throws ProcessTaskException {
		boolean isDone = future.IsDone();
		logger.debug("EDTask: node=" + node.getName() + " future.isDone="
				+ isDone);
		if (isDone == false) {
			ve.DoED();
			return PausableTask.Result.Pauze;
		} else {
			return ProcessTask.Result.Next;
		}
	}

	@Override
	public void cancel() {
		// do nothing
	}

}