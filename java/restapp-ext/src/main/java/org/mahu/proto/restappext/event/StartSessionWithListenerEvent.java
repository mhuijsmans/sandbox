package org.mahu.proto.restappext.event;

import org.mahu.proto.restapp.engine.SessionId;

public class StartSessionWithListenerEvent extends StartSessionEvent {
	final SingleStepListener listener;

	public StartSessionWithListenerEvent(SessionId id, String nameProtocol, final SingleStepListener listener) {
		super(id, nameProtocol, true);
		this.listener = listener;
	}
	
	public StartSessionWithListenerEvent(final SessionId id, final String nameSystem, final String nameProtocol, final SingleStepListener listener) {
		super(id, nameSystem, nameProtocol, true);
		this.listener = listener;
	}
	
	public SingleStepListener GetSingleStepListener() {
		return listener;
	}

}
