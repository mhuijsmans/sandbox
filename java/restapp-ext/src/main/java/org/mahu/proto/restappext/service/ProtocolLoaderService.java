package org.mahu.proto.restappext.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mahu.proto.commons.StopWatch;
import org.mahu.proto.restapp.model.ProcessAlreadyExistsException;
import org.mahu.proto.restapp.model.ProcessDefinition;
import org.mahu.proto.restapp.model.impl.ProcessBuilderException;
import org.mahu.proto.restapp.util.ReflectionUtil.ReflectionUtilException;
import org.mahu.proto.restapp.xmladapter.ActivitiBmpn2XmlReader;
import org.mahu.proto.restappext.event.LoadProtocolCompletedEvent;
import org.mahu.proto.restappext.event.LoadProtocolEvent;
import org.mahu.proto.restappext.event.LoadProtocolWorkerCompletedEvent;
import org.mahu.proto.restappext.event.LoadProtocolWorkerEvent;
import org.mahu.proto.restappext.eventbus.AsyncEventBus;

import com.google.common.eventbus.Subscribe;

public class ProtocolLoaderService {
	protected final static Logger LOGGER = LogManager.getLogger(ProtocolLoaderService.class.getName());

	private final AsyncEventBus eventBus;
	private final AsyncEventBus workerEventBus;
	private final Map<String, ProtocolData> protocols = new HashMap<>();

	class ProtocolData {
		private final ProcessDefinition processDefinition;
		private final List<LoadProtocolEvent> pending;

		ProtocolData() {
			this.processDefinition = null;
			this.pending = new LinkedList<>();
		}

		ProtocolData(final ProcessDefinition processDefinition) {
			this.processDefinition = processDefinition;
			this.pending = null;
		}
	}

	public ProtocolLoaderService(AsyncEventBus event,
			AsyncEventBus workerEventBus) {
		this.eventBus = event;
		this.workerEventBus = workerEventBus;
	}

	@Subscribe
	public void HandleEvent(final LoadProtocolEvent event) {
		LOGGER.debug("ENTER: event=" + event.getClass().getName());
		String protocol = event.GetStartSessionEvent().GetProtocolName();
		ProtocolData protocolData = protocols.get(protocol);
		if (protocolData == null) {
			protocolData = new ProtocolData();
			protocols.put(protocol, protocolData);
			workerEventBus.Post(new LoadProtocolWorkerEvent(event));
		}
		if (protocolData.processDefinition == null) {
			LOGGER.debug("Adding to pending list");
			protocolData.pending.add(event);
		} else {
			LOGGER.debug("Sending LoadProtocolCompletedEvent");
			eventBus.Post(new LoadProtocolCompletedEvent(event,
					protocolData.processDefinition));
		}
		LOGGER.debug("LEAVE: event=" + event.getClass().getName());
	}

	@Subscribe
	public void HandleEvent(final LoadProtocolWorkerCompletedEvent event) {
		LOGGER.debug("ENTER: event=" + event.getClass().getName());
		String protocol = event.GetLoadProtocolEvent().GetStartSessionEvent()
				.GetProtocolName();
		ProcessDefinition processDefinition = event.GetProcessDefinition();
		ProtocolData protocolData = protocols.get(protocol);
		if (processDefinition != null) {
			if (protocolData.processDefinition == null) {
				protocols.put(protocol, new ProtocolData(processDefinition));
			} else {
				// already known
			}
		} else {
			// protocolDefinition is not known or contains an error.
			protocols.remove(protocol);
		}
		for (LoadProtocolEvent pendingEvent : protocolData.pending) {
			eventBus.Post(new LoadProtocolCompletedEvent(pendingEvent,
					processDefinition));
		}
		LOGGER.debug("LEAVE: event=" + event.getClass().getName());
	}

	@Subscribe
	public void HandleEvent(LoadProtocolWorkerEvent event) {
		final String protocolName = event.GetLoadProtocolEvent()
				.GetStartSessionEvent().GetProtocolName();
		LOGGER.debug("ENTER: event=" + event.getClass().getName() + " protocol="
				+ protocolName);
		ProcessDefinition processDefinition = null;
		try {
			processDefinition = ReadProcessXML(protocolName);
		} catch (ProcessBuilderException | ProcessAlreadyExistsException
				| IOException | XMLStreamException | ReflectionUtilException
				| URISyntaxException e) {
			LOGGER.info("Failed to load protocol=" + protocolName);
		}
		eventBus.Post(new LoadProtocolWorkerCompletedEvent(event
				.GetLoadProtocolEvent(), processDefinition));
		LOGGER.debug("LEAVE");
	}

	private ProcessDefinition ReadProcessXML(String name)
			throws UnsupportedEncodingException, IOException,
			XMLStreamException, ProcessBuilderException,
			ReflectionUtilException, ProcessAlreadyExistsException,
			URISyntaxException {
		LOGGER.debug("ENTER");
		// todo: support different format (resource, file, url)
		ProcessDefinition processDefinition = null;
		// Not know yet
		java.net.URL url = getClass().getClassLoader().getResource(name);
		if (url != null) {
			java.nio.file.Path resPath = java.nio.file.Paths.get(url.toURI());
			String xml = new String(java.nio.file.Files.readAllBytes(resPath),
					"UTF8");
			if (xml.length() >= 0) {
				// Read XML
				StopWatch sw1 = new StopWatch();
				ActivitiBmpn2XmlReader adapter = new ActivitiBmpn2XmlReader();
				adapter.readXml(xml);
				processDefinition = adapter.convertLoadedModel();
				sw1.Clock();
				LOGGER.info("ReadProcessXML in time=" + sw1.ElapsedTimeInMS()
						+ " ms");
			}
		}
		LOGGER.debug("LEAVE");
		return processDefinition;
	}
}
