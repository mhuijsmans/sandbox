package org.mahu.proto.restapp.engine;

/**
 * The implementation has equals, hashcode and toString() implemented.
 * It can be used as key in e.g. a hasmMap;  
 */
public interface SessionId {

	/**
	 * Wait for completion of the session.
	 * @return resultData set by the application that integrates the workflow.
	 * @throws InterruptedException
	 */
	public Object WaitForCompletion() throws InterruptedException;
}
