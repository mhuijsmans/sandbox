package org.mahu.proto.restappext.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mahu.proto.restapp.model.Node;
import org.mahu.proto.restapp.model.ProcessTask;

public class ExceptionTask implements ProcessTask {

	final static Logger logger = LogManager.getLogger(ExceptionTask.class.getName());

	// No Inject to get an exception
	Node node = null;

	@Override
	public Enum<?> execute() throws ProcessTaskException {
		logger.debug("ExceptionTask ENTER");
		node.getName();
		logger.debug("ExceptionTask LEAVE");
		return Result.Next;
	}

}