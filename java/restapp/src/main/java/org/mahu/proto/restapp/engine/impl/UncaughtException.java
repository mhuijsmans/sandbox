package org.mahu.proto.restapp.engine.impl;

import org.mahu.proto.restapp.engine.WorkflowEngineException;

/**
 * The workflow engine can caught an runtime exception from a ProcessTask  
 */
public class UncaughtException extends WorkflowEngineException {
	
	private static final long serialVersionUID = -5302064557214716147L;

	UncaughtException(final String description, final Exception e) {
		super(description, e);
	}

}
