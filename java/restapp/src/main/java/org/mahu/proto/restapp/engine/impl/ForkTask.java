package org.mahu.proto.restapp.engine.impl;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mahu.proto.restapp.engine.JobList;
import org.mahu.proto.restapp.engine.Session;
import org.mahu.proto.restapp.engine.WorkflowEngine;
import org.mahu.proto.restapp.model.Node;
import org.mahu.proto.restapp.model.ProcessPath;
import org.mahu.proto.restapp.model.ProcessTask;
import org.mahu.proto.restapp.model.annotation.ProcessTaskResult;
import org.mahu.proto.restapp.model.impl.ForkNodeImpl;

/**
 * Class that is used by Engine for Fork
 */
public final class ForkTask implements ProcessTask {

	final static Logger logger = LogManager.getLogger(WorkflowEngine.class.getName());

	@ProcessTaskResult
	public enum Result {
		Fork
	}

	@Inject
	private Node node;

	@Inject
	private Session session;

	@Inject
	private JobList jobExecutor;

	@Override
	public Enum<?> execute() throws ProcessTaskException {
		ForkNodeImpl forkNode = (ForkNodeImpl) node;
		ForkInstance forkInstance = new ForkInstance(forkNode.getName(),
				forkNode.getProcessPathNames().length);
		logger.debug("Fork, " + forkInstance);
		((SessionImpl) session).add(forkNode.getName(), forkInstance);
		// For the forkpath's start a new job job.
		for (String processPathName : forkNode.getProcessPathNames()) {
			ProcessPath path = node.getProcessDefinition().getProcessPath(
					processPathName);
			jobExecutor.add(new JobImpl(session.getSessionId(), path
					.getFirstNode()));
		}
		return Result.Fork;
	}

}