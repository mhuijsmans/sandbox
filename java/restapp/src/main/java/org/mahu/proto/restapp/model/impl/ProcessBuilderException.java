package org.mahu.proto.restapp.model.impl;

import org.mahu.proto.restapp.engine.WorkflowEngineException;

public class ProcessBuilderException extends WorkflowEngineException {

	private static final long serialVersionUID = -4665731019928618465L;
	
	ProcessBuilderException(final String description) {
		super(description);
	}	
	
	ProcessBuilderException(final String description, final Exception e) {
		super(description, e);
	}

}
