package org.mahu.proto.restappext.event;

import org.mahu.proto.restapp.engine.SessionId;

public class WorkflowFutureReadyEvent extends Event {

	public WorkflowFutureReadyEvent(SessionId id) {
		super(id);
	}
}
