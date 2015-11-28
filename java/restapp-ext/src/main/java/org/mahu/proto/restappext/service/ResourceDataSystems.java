package org.mahu.proto.restappext.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mahu.proto.restappext.event.StartWorkflowSessionEvent;

public class ResourceDataSystems implements ResourceData {
	
	protected final static Logger LOGGER = LogManager.getLogger(ResourceDataSystems.class.getName());
	
	private Map<String, Map<String, Object>> systemData = new HashMap<>();

	public void AddSystemSettings(final String name,
			final Map<String, Object> data) {
		systemData.put(name, data);
	}

	public boolean AddResourceData(final StartWorkflowSessionEvent event) {
		String systemName = event.GetSystemName();
		if (systemName!=null) {
			Map<String, Object> data = systemData.get(systemName);
			if (data !=null) {
				LOGGER.debug("Adding data for system="+systemName);
				event.PutAll(data);
				return true;
			} else {
				LOGGER.debug("No data available for system="+systemName);
			}
		} else {
			LOGGER.debug("SystemName not provided");
		}
		return false;
	}
}
