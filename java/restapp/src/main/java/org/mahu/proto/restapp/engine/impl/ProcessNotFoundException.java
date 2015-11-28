package org.mahu.proto.restapp.engine.impl;

import org.mahu.proto.restapp.engine.WorkflowEngineException;

public class ProcessNotFoundException extends WorkflowEngineException {

	private static final long serialVersionUID = -4665731019928618465L;
	
	ProcessNotFoundException(final String description) {
		super(description);
	}

}
