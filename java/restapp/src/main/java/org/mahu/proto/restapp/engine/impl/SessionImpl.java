package org.mahu.proto.restapp.engine.impl;

import static com.google.common.base.Preconditions.checkState;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mahu.proto.commons.WaitForTrue;
import org.mahu.proto.restapp.engine.Session;
import org.mahu.proto.restapp.engine.SessionId;
import org.mahu.proto.restapp.engine.WorkflowEngine;
import org.mahu.proto.restapp.engine.WorkflowEngine.State;
import org.mahu.proto.restapp.model.ProcessDefinition;

class SessionImpl implements Session {
	
	protected final static Logger logger = LogManager.getLogger(SessionImpl.class.getName());

	private WorkflowEngine.State state = State.INIT;
	private final WaitForTrue waitForTrue = new WaitForTrue();
	private Exception exception = null;
	private final Map<String, Object> context = new HashMap<String, Object>();
	private final ProcessDefinition processDefiniton;
	private final SessionId id;

	public SessionImpl(final SessionId id,
			final ProcessDefinition processDefiniton) {
		this.id = id;
		this.processDefiniton = processDefiniton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public WorkflowEngine.State getState() {
		return state;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SessionId getSessionId() {
		return id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProcessDefinition getPresidentProcessDefinition() {
		return processDefiniton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void waitForCompletion(final int maxWait) {
		waitForTrue.waitForTrue(maxWait);
	}

	public void setStateReady() {
		logger.debug("");
		if (state != WorkflowEngine.State.TERMINATED) {
			checkState(state == WorkflowEngine.State.INIT
					|| state == WorkflowEngine.State.EXECUTING_JOBS_EXIST);
			state = State.TERMINATED;
			waitForTrue.setTrue();
		}
	}

	public void setStateReady(final Exception e) {
		logger.debug("ENTER exception="+e);
		logger.error("Session aborted id="+id, e);
		checkState(exception == null);
		checkState(state == WorkflowEngine.State.INIT || state == WorkflowEngine.State.EXECUTING_JOBS_EXIST || state == WorkflowEngine.State.EXECUTING_PAUZED);
		exception = e;
		state = State.ABORTED;
		waitForTrue.setTrue();
	}

	@Override
	public Exception getException() {
		return exception;
	}

	public void setStateJobsExists() {
		checkState(state == WorkflowEngine.State.INIT || state == WorkflowEngine.State.EXECUTING_JOBS_EXIST || state == WorkflowEngine.State.EXECUTING_PAUZED);
		state = State.EXECUTING_JOBS_EXIST;
	}

	public void setStatePauzed() {
		checkState(state == WorkflowEngine.State.EXECUTING_JOBS_EXIST || state == WorkflowEngine.State.EXECUTING_PAUZED);
		state = State.EXECUTING_PAUZED;
	}
	
	public boolean isStateReady() {
		return state == WorkflowEngine.State.TERMINATED ||  state == WorkflowEngine.State.ABORTED;
	}	

	public boolean isStateJobsExistsOrPauzed() {
		return state == WorkflowEngine.State.EXECUTING_JOBS_EXIST ||  state == WorkflowEngine.State.EXECUTING_PAUZED;
	}
	
	public boolean isStateJobsExists() {
		return state == WorkflowEngine.State.EXECUTING_JOBS_EXIST;
	}
	
	public boolean isStatePauzed() {
		return state == WorkflowEngine.State.EXECUTING_PAUZED;
	}
	
	public boolean isStateAborted() {
		return state == WorkflowEngine.State.ABORTED;
	}	

	public void add(final String key, final Object value) {
		context.put(key, value);
	}

	public void remove(final String key) {
		context.remove(key);
	}

	public Object get(final String key) {
		return context.get(key);
	}

	public void copyData(final Map<String, Object> data) {
		if (data != null) {
			context.putAll(data);
		}
	}

}
