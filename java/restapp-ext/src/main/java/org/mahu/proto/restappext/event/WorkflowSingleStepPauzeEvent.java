package org.mahu.proto.restappext.event;

import org.mahu.proto.restapp.engine.SessionId;

public class WorkflowSingleStepPauzeEvent extends Event {

	public WorkflowSingleStepPauzeEvent(final SessionId id) {
		super(id);
	}

}
