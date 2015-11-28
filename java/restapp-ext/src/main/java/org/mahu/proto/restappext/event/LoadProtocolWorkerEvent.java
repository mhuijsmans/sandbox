package org.mahu.proto.restappext.event;

public class LoadProtocolWorkerEvent {
	private final LoadProtocolEvent event;

	public LoadProtocolWorkerEvent(LoadProtocolEvent event) {
		this.event = event;
	}

	public LoadProtocolEvent GetLoadProtocolEvent() {
		return event;
	}
}
