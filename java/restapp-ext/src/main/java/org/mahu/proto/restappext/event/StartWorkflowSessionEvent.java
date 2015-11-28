package org.mahu.proto.restappext.event;

import java.util.HashMap;
import java.util.Map;

import org.mahu.proto.restapp.model.ProcessDefinition;

public class StartWorkflowSessionEvent extends Event {
	private final ProcessDefinition processDefinition;
	private final boolean isSingleStep;
	private Map<String, Object> data;
	private final String systemName;

	public StartWorkflowSessionEvent(final Event event,
			final String systemName, final ProcessDefinition processDefinition,
			final boolean singleStep, final Map<String, Object> data) {
		super(event.GetSessionId());
		this.processDefinition = processDefinition;
		this.isSingleStep = singleStep;
		this.data = data;
		this.systemName = systemName;
	}

	public void PutAll(final Map<String, Object> data) {
		if (this.data == null) {
			this.data = new HashMap<>();
		}
		this.data.putAll(data);
	}

	public ProcessDefinition GetProcessDefinition() {
		return processDefinition;
	}

	public boolean IsSingleStep() {
		return isSingleStep;
	}

	public Map<String, Object> GetData() {
		return data;
	}

	public String GetSystemName() {
		return systemName;
	}

	public boolean equals(Object o) {
		if (o instanceof StartWorkflowSessionEvent) {
			StartWorkflowSessionEvent e = (StartWorkflowSessionEvent) o;
			if (id.equals(e.id)
					&& processDefinition.getName().equals(
							e.processDefinition.getName())
					&& systemName.equals(e.systemName)
					&& isSingleStep == (e.isSingleStep)) {
				if (data!=null && e.data!=null) {
					return data.size() == e.data.size();
				}
				if (data==null && e.data==null) {
					return true;
				}				
			}
		}
		return false;
	}
}
