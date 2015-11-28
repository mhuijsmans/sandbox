package org.mahu.proto.restapp.engine.impl;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mahu.proto.restapp.engine.WorkflowEngine;
import org.mahu.proto.restapp.model.CloseableTask;

/**
 * Class that holds a list of CloseableTask.
 */
final class CloseableTasks {

	final static Logger logger = LogManager.getLogger(WorkflowEngine.class.getName());

	private final List<CloseableTask> CloseableTask = new LinkedList<CloseableTask>();

	void add(final CloseableTask task) {
		CloseableTask.add(task);
	}

	void callCloseOnTasksAndClear() {
		for (CloseableTask task : CloseableTask) {
			try {
				task.close();
			} catch (Exception e) {
				logger.error("Task " + task.getClass().getName()
						+ " when closed, threw Exception "
						+ e.getLocalizedMessage());
			}
		}
		CloseableTask.clear();
	}

}
