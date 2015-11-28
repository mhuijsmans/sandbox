package org.mahu.proto.restappext.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mahu.proto.restapp.engine.SessionId;
import org.mahu.proto.restappext.event.StartSessionCompletedEvent;
import org.mahu.proto.restappext.event.StartSessionEvent;

/**
 * ResourceManagement, that allows a single request for a system.
 */
public class ResourceManagementOneRequestPerSystem implements
		ResourceManagementAlgorithm {

	protected final static Logger LOGGER = LogManager.getLogger(ResourceManagementOneRequestPerSystem.class.getName());

	private Map<SessionId, String> sessionIdToSystemName = new HashMap<>();
	private Map<String, SessionId> systemNameToSessiondId = new HashMap<>();

	public boolean Allocate(final StartSessionEvent event) {
		String systemName = event.GetNameSystem();
		SessionId id = event.GetSessionId();
		if (systemNameToSessiondId.containsKey(systemName)) {
			LOGGER.debug("allocate resource returned false for system="
					+ systemName);
			return false;
		}
		sessionIdToSystemName.put(id, systemName);
		systemNameToSessiondId.put(systemName, id);
		LOGGER.debug("allocate resource returned true for system=" + systemName);
		return true;
	}

	public void Release(final StartSessionCompletedEvent event) {
		SessionId id = event.GetSessionId();
		String systemName = sessionIdToSystemName.remove(id);
		if (systemName != null) {
			LOGGER.debug("release resource for system=" + systemName);
			systemNameToSessiondId.remove(systemName);
		}
	}
}
