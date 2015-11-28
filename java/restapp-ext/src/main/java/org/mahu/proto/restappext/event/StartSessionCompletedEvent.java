package org.mahu.proto.restappext.event;

public class StartSessionCompletedEvent extends Event {

	/**
	 * Outcome of the StartSessionEvent
	 */
	public enum Result {
		OK, ERROR, NORESOURCES
	}

	private final Result result;
	private final String details;

	public StartSessionCompletedEvent(final Event event, final Result result) {
		super(event);
		this.result = result;
		this.details = null;
	}
	
	public StartSessionCompletedEvent(final Event event, final Result result, final String details) {
		super(event);
		this.result = result;
		this.details = details;
	}

	public Result GetResult() {
		return result;
	}
	
	public String GetDetails() {
		return details;
	}
	
	public boolean equals(Object o) {
		if (o instanceof StartSessionCompletedEvent) {
			StartSessionCompletedEvent e = (StartSessionCompletedEvent)o;
			return id.equals(e.id) && result == e.result;
		}
		return false;
	}
	
	@Override
	public String toString() {
		switch(result) {
		case OK:
			return super.toString()+" result=OK";
		case NORESOURCES:
			return super.toString()+" result=NORESOURCES";
		default:
			return super.toString()+" result=ERROR";	
		}
	}

}
