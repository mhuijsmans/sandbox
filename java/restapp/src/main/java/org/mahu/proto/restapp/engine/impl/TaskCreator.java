package org.mahu.proto.restapp.engine.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mahu.proto.restapp.engine.ExecutionErrorException;
import org.mahu.proto.restapp.engine.WorkflowEngine;
import org.mahu.proto.restapp.model.Node;
import org.mahu.proto.restapp.model.ProcessTask;

/**
 * Class creates instances of a ProcessTask and processes Annotations
 */
class TaskCreator {

	final static Logger logger = LogManager.getLogger(WorkflowEngine.class.getName());

	ProcessTask createTaskInstance(final SessionImpl session,final Node node) {
		ProcessTask task = null;
		try {
			task = node.getProcessTaskClass().newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			session.setStateReady(new ExecutionErrorException(
					"Failed to create instance of "
							+ node.getProcessTaskClass().getName(),e));
		}
		return task;
	}

}
