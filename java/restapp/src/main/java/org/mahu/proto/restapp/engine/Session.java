package org.mahu.proto.restapp.engine;

import org.mahu.proto.restapp.model.ProcessDefinition;

/**
 * Session represents a executing process instance.
 */
public interface Session {

	/**
	 * Returns current state
	 * 
	 * @return
	 */
	public WorkflowEngine.State getState();

	/**
	 * Returns SessionId
	 * 
	 * @return
	 */
	public SessionId getSessionId();

	/**
	 * Returns ProcessDefinition
	 * 
	 * @return
	 */
	public ProcessDefinition getPresidentProcessDefinition();

	/**
	 * Wait a given time for the session to complete. By using a timer it ensure
	 * that the calls ends.
	 * 
	 * @param maxWait
	 *            > 0 (in ms)
	 */
	public void waitForCompletion(final int maxWait);

	/**
	 * Get Exception that resulted in aborting the current session.
	 * 
	 * @return
	 */
	public Exception getException();

	/**
	 * Retrieve data stored
	 * 
	 * @param key
	 * @return
	 */
	public Object get(final String key);
}
