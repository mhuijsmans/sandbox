package org.mahu.proto.restapp.model;

import org.mahu.proto.restapp.engine.WorkflowEngineException;

public class ProcessAlreadyExistsException extends WorkflowEngineException {

	private static final long serialVersionUID = -3644655466747117843L;
	
	public ProcessAlreadyExistsException(final String description) {
		super(description);
	}


}
