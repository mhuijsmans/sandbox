package org.mahu.proto.restapp.engine;

/**
 * The workflow engine has detected during execution an error where is (decided
 * that) it can not continue.
 */
public class ExecutionErrorException extends WorkflowEngineException {

	private static final long serialVersionUID = -1362610556737285052L;

	public ExecutionErrorException(String description) {
		super(description);
	}

	public ExecutionErrorException(String description, final Exception e) {
		super(description, e);
	}

}
