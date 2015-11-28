package org.mahu.proto.restapp.engine;

import org.mahu.proto.restapp.model.Node;
import org.mahu.proto.restapp.model.PausableTask;
import org.mahu.proto.restapp.model.ProcessDefinition;

public interface Job {

	public long getId();

	public SessionId getSessionId();

	/**
	 * Execute Node
	 * @return
	 */
	public Node getNode();

	/**
	 * A pausable task may have completed.
	 *  
	 * @return the pausable task, cann be null
	 */
	public PausableTask getProcessTask();

	/**
	 * An process consists of 1-n ProcessDefinitions. It has a single President.
	 * It can have children, i.e. each fork results in a ProcessDefinition for
	 * that path.
	 * 
	 * @return the ProcessDefinition
	 */
	public ProcessDefinition getProcessDefinition();
}
