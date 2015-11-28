package org.mahu.proto.restapp.model;

/**
 * By implementing this interface a ProcessTask implementation once invoked, 
 * will receive a close() call when the WorkflowEngine has completed 
 * a session for a executed ProcessDefinition. 
 */
public interface CloseableTask {
	
	public void close();

}
