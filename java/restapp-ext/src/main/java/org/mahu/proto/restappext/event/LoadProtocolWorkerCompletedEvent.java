package org.mahu.proto.restappext.event;

import org.mahu.proto.restapp.model.ProcessDefinition;

public class LoadProtocolWorkerCompletedEvent  extends Event {
	private final LoadProtocolEvent event;
	private final ProcessDefinition processDefinition;

	public LoadProtocolWorkerCompletedEvent(LoadProtocolEvent event,
			ProcessDefinition processDefinition) {
		super(event);
		this.event = event;
		this.processDefinition = processDefinition;
	}

	public LoadProtocolEvent GetLoadProtocolEvent() {
		return event;
	}

	public ProcessDefinition GetProcessDefinition() {
		return processDefinition;
	}

}
