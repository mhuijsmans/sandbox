package org.mahu.proto.restapp.engine.impl;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mahu.proto.restapp.engine.PausedTasksList;
import org.mahu.proto.restapp.engine.WorkflowEngine;
import org.mahu.proto.restapp.model.PausableTask;

/**
 * Class that holds a list of PausableProcessTask.
 */
final class PausedTasksListImpl implements PausedTasksList {

	final static Logger logger = LogManager.getLogger(WorkflowEngine.class.getName());

	private final List<PausableTask> pausedTasks = new LinkedList<PausableTask>();
	private final Object lock;

	PausedTasksListImpl(Object lock) {
		this.lock = lock;
	}

	@Override
	public int size() {
		synchronized (lock) {
			return pausedTasks.size();
		}
	}

	@Override
	public void add(final PausableTask task) {
		synchronized (lock) {
			pausedTasks.add(task);
		}
	}

	@Override
	public void remove(final PausableTask task) {
		synchronized (lock) {

			pausedTasks.remove(task);
		}
	}

	@Override
	public void cancelTasksAndCLear() {
		synchronized (lock) {
			for (PausableTask task : pausedTasks) {
				try {
					task.cancel();
				} catch (Exception e) {
					logger.error("Task " + task.getClass().getName()
							+ " when cancelled, threw Exception "
							+ e.getLocalizedMessage());
				}
			}
			pausedTasks.clear();
		}
	}

}
