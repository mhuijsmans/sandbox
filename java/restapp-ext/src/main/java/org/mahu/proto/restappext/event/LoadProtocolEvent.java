package org.mahu.proto.restappext.event;

public class LoadProtocolEvent extends Event {
	private final StartSessionEvent startSessionEvent;

	public LoadProtocolEvent(final StartSessionEvent startSessionEvent) {
		super(startSessionEvent);
		this.startSessionEvent = startSessionEvent;
	}
	
	public StartSessionEvent GetStartSessionEvent() {
		return startSessionEvent;
	}
	
	public boolean equals(Object o) {
		if (o instanceof LoadProtocolEvent) {
			LoadProtocolEvent lpEvent = (LoadProtocolEvent)o;
			return startSessionEvent.equals(lpEvent.GetStartSessionEvent());
		}
		return false;
	}
}
