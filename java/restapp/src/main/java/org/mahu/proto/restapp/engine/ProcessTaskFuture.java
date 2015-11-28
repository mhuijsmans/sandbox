package org.mahu.proto.restapp.engine;

public interface ProcessTaskFuture {
	
	/**
	 * @returns true if the async task used by the ProcessTask has completed.
	 */
	boolean IsDone();
	
	/**
	 * Method invoked by the async task to signal completion
	 */
	public void AsyncTaskHasCompleted();
	
	/**
	 * @return SessionId identifying the session to which the future belongs.
	 */
	public SessionId GetSessionId();
}
