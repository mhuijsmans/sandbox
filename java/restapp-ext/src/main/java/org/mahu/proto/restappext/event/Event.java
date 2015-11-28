package org.mahu.proto.restappext.event;

import org.mahu.proto.restapp.engine.SessionId;

public class Event {
	/**
	 * Session to which the Event belongs
	 */
	protected final SessionId id;
	
	public Event(Event event) {
		this.id = event.id;
	}		

	public Event(SessionId id) {
		this.id = id;
	}	
	
	public SessionId GetSessionId() {
		return id;
	}
	
	public String toString() {
		return this.getClass().getSimpleName()+"-"+id;
	}
	
	public boolean equals(Object o) {
		return o instanceof Event && ((Event)o).id.equals(id);
	}
}
