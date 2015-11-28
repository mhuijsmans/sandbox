package org.mahu.proto.restapp.engine.impl;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mahu.proto.restapp.engine.JobList;
import org.mahu.proto.restapp.engine.Session;
import org.mahu.proto.restapp.engine.WorkflowEngine;
import org.mahu.proto.restapp.model.Node;
import org.mahu.proto.restapp.model.ProcessTask;
import org.mahu.proto.restapp.model.impl.JoinNodeImpl;

/**
 * Class that is used by Engine for Join
 */
public final class JoinTask implements ProcessTask {

	final static Logger logger = LogManager.getLogger(WorkflowEngine.class.getName());

	@Inject
	private Node node;

	@Inject
	private Session session;

	@Inject
	private JobList jobExecutor;

	@Override
	public Enum<?> execute() throws ProcessTaskException {
		JoinNodeImpl joinNode = (JoinNodeImpl) node;
		JoinNodeImpl parentJoinNode = joinNode.getParent();
		// Jointask be be executed as last task for a ProcessPath 
		if (parentJoinNode != null) {
			// Executing in a ProcessPath.
			// When all fork-path's have completed, continue in 
			// the parent ProcessDefinition with the forked/join.
			// TODO: move the ForkInstance to the parentJoinNode. 
			// Now there is a risk that the name is not unique. 
			ForkInstance forkInstance = (ForkInstance) session
					.get(parentJoinNode.getForkName());
			//logger.debug("Join, " + forkInstance);
			if (forkInstance.processPathIsReady() == 0) {
				jobExecutor.add(new JobImpl(session.getSessionId(), parentJoinNode));
				((SessionImpl)session).remove(parentJoinNode.getForkName());
			}
			return ProcessTask.Result.Null;
		} else {
			// Continue execution in the ProcessDefinition that contains the Fork/Join.
			// This ProcessDefinition can be the President or a ProcessPath. 
			return ProcessTask.Result.Next;
		}
	}

}