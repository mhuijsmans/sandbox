package org.mahu.proto.restapp.engine.impl;

import java.util.concurrent.atomic.AtomicLong;

import org.mahu.proto.restapp.engine.Job;
import org.mahu.proto.restapp.engine.SessionId;
import org.mahu.proto.restapp.model.Node;
import org.mahu.proto.restapp.model.PausableTask;
import org.mahu.proto.restapp.model.ProcessDefinition;

public class JobImpl implements Job {

	private static AtomicLong jobIdGenerator = new AtomicLong();

	private final SessionId sessionId;
	private final Node node;
	private final PausableTask processTask;
	private final long id = jobIdGenerator.getAndIncrement();

	/**
	 * Create a Job for the provided node.
	 * 
	 * @param aSession
	 * @param aNode
	 */
	public JobImpl(final SessionId sessionId, final Node aNode) {
		this.sessionId = sessionId;
		this.node = aNode;
		this.processTask = null;
	}

	/**
	 * Create a Job for a node whose processTask paused and now has resumed
	 * work.
	 * 
	 * @param aNode
	 * @param aProcessTask
	 */
	public JobImpl(final SessionId session, final Node aNode,
			final PausableTask aProcessTask) {
		this.node = aNode;
		this.processTask = aProcessTask;
		this.sessionId = session;
	}

	@Override
	public Node getNode() {
		return node;
	}

	@Override
	public PausableTask getProcessTask() {
		return processTask;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public SessionId getSessionId() {
		return getSessionId();
	}

	public ProcessDefinition getProcessDefinition() {
		return node.getProcessDefinition();
	}
}
