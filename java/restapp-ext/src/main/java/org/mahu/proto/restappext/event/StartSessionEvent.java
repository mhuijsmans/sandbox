package org.mahu.proto.restappext.event;

import java.util.Map;

import org.mahu.proto.restapp.engine.SessionId;

public class StartSessionEvent extends Event {
	private final String nameSystem;
	private final String nameProtocol;
	private final boolean isSingleStep;
	private final Map<String, Object> data;

	public StartSessionEvent(final SessionId id, final String nameProtocol,
			final boolean singleStep) {
		super(id);
		this.nameSystem = EventConst.LOCALSYSTEM;
		this.nameProtocol = nameProtocol;
		this.isSingleStep = singleStep;
		this.data = null;
	}

	public StartSessionEvent(final SessionId id, final String nameSystem,
			final String nameProtocol, final boolean singleStep) {
		this(id, nameSystem, nameProtocol, singleStep, null);
	}
	
	public StartSessionEvent(final SessionId id, final String nameSystem,
			final String nameProtocol, final boolean singleStep, final Map<String, Object> data) {
		super(id);
		this.nameSystem = nameSystem;
		this.nameProtocol = nameProtocol;
		this.isSingleStep = singleStep;
		this.data = data;
	}

	public String GetProtocolName() {
		return nameProtocol;
	}

	public boolean IsSingleStep() {
		return isSingleStep;
	}

	public String GetNameSystem() {
		return nameSystem;
	}
	
	public Map<String, Object> GetData() {
		return data;
	}	

	public boolean equals(Object o) {
		if (o instanceof StartSessionEvent) {
			StartSessionEvent e = (StartSessionEvent) o;
			return id.equals(e.id) && nameProtocol.equals(e.nameProtocol)
					&& nameSystem.equals(e.nameSystem)
					&& isSingleStep == e.isSingleStep;
		}
		return false;
	}
}