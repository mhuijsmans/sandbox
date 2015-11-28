package org.mahu.proto.restapp.engine;

public class WorkflowEngineException extends Exception {

	private static final long serialVersionUID = 1906510722978215581L;
	
	public WorkflowEngineException(final String description) {
		super(description);
	}
	
	public WorkflowEngineException(final String description, final Exception e) {
		super(description, e);
	}
	
}
