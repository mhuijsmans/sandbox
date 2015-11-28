package org.mahu.proto.restapp.engine.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mahu.proto.restapp.engine.JobList;
import org.mahu.proto.restapp.engine.ProcessTaskFuture;
import org.mahu.proto.restapp.engine.SessionId;
import org.mahu.proto.restapp.engine.WorkflowEngine;
import org.mahu.proto.restapp.model.Node;
import org.mahu.proto.restapp.model.PausableTask;

public class ProcessTaskFutureImpl implements ProcessTaskFuture {

	final static Logger logger = LogManager.getLogger(ProcessTaskFutureImpl.class.getName());

	private boolean isDone = false;
	// Node owning the task
	private final Node node;
	// task that was paused
	private final PausableTask task;
	// Joblist is the destination where a finished can post completion
	private final JobList joblist;
	// SessiondId is the Id is the session
	private final SessionId id;	
	// SessiondId is the Id is the session
	private final WorkflowEngine.Listener observer;

	ProcessTaskFutureImpl(final  SessionId id, final Node node, final PausableTask task,
			final JobList joblist, final  WorkflowEngine.Listener observer) {
		this.task = task;
		this.node = node;
		this.joblist = joblist;
		this.id = id;
		this.observer = observer;
	}

	@Override
	public synchronized boolean IsDone() {
		return isDone;
	}
	
	@Override
	public SessionId GetSessionId() {
		return id;
	}

	@Override
	public void AsyncTaskHasCompleted() {
		boolean tmpIsDone;
		synchronized (this) {
			tmpIsDone = isDone;
			isDone = true;
		}
		if (!tmpIsDone) {
			logger.trace("AsyncTaskHasCompleted, adding a new Job");
			joblist.add(new JobImpl(id, node, task));
			observer.ProcessTaskFutureHasCompleted(id);
		} else {
			logger.error("AsyncTaskHasCompleted AGAIN, ignoring notification");
		}
	}

}
