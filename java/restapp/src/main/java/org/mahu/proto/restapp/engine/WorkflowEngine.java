package org.mahu.proto.restapp.engine;

import java.util.Map;

import org.mahu.proto.restapp.model.ProcessDefinition;

/**
 * The workflow engine2 interface.
 */
public interface WorkflowEngine {

	public enum State {
		INIT, EXECUTING_JOBS_EXIST, EXECUTING_PAUZED, TERMINATED, ABORTED
	};

	public interface Listener {
		public void ProcessTaskFutureHasCompleted(SessionId id);
	};

	/**
	 * Initialize object with data. An Object can be initialized only once.
	 * 
	 * @param id
	 * @param processDefiniton
	 * @param data
	 * @param observer
	 */
	public void Init(final SessionId id,
			final ProcessDefinition processDefiniton,
			final Map<String, Object> data, final Listener observer);

	/**
	 * Execute one job
	 * 
	 * @return state, which can be any state other than INIT.
	 */
	public State ExecuteOneJob();

	/**
	 * Execute jobs as long as jobs exists and no error occurs.
	 * 
	 * @return state which can be EXECUTING_PAUZED, TERMINATED, ABORTED
	 */
	public State ExecuteJobs();

	/**
	 * Execute jobs as long as jobs exists and no error occurs.
	 * 
	 * @return state which can be EXECUTING_PAUZED, TERMINATED, ABORTED
	 * @throws InterruptedException
	 */
	public State ExecuteJobsUntilFinalStateReached(int maxWait)
			throws InterruptedException;

	/**
	 * @return current state
	 */
	public State GetState();

	/**
	 * @return for the top-level job that will be executes next, the Node.
	 */
	// TODO: check to remove
	// public Node PeekAtNodeToExecute();
}
