package org.mahu.proto.restappext.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mahu.proto.restappext.event.LoadProtocolCompletedEvent;
import org.mahu.proto.restappext.event.LoadProtocolEvent;
import org.mahu.proto.restappext.event.StartSessionCompletedEvent;
import org.mahu.proto.restappext.event.StartSessionCompletedEvent.Result;
import org.mahu.proto.restappext.event.StartSessionEvent;
import org.mahu.proto.restappext.event.StartWorkflowSessionEvent;
import org.mahu.proto.restappext.eventbus.AsyncEventBus;

import com.google.common.eventbus.Subscribe;

/**
 * Responsibilities - analyze incoming request to determine if resources are
 * available. - reserve resources - add resource specific settings, such as
 * addresses, etc.
 */
public class ResourceManagerService {
	protected final static Logger LOGGER = LogManager.getLogger(ResourceManagerService.class.getName());

	private final AsyncEventBus eventBus;
	private final ResourceManagementAlgorithm resourceManagementAlgorithm;
	private final ResourceData resourceData;

	public ResourceManagerService(final AsyncEventBus eventBus) {
		this.eventBus = eventBus;
		this.resourceManagementAlgorithm = new ResourceManagementNoLimit();
		this.resourceData = new ResourceDataNoop();
	}

	public ResourceManagerService(final AsyncEventBus eventBus,
			final ResourceManagementAlgorithm resourceManagementAlgorithm,
			final ResourceData resourceData) {
		this.eventBus = eventBus;
		this.resourceManagementAlgorithm = (resourceManagementAlgorithm != null) ? resourceManagementAlgorithm
				: new ResourceManagementNoLimit();
		this.resourceData = (resourceData != null) ? resourceData
				: new ResourceDataNoop();
	}

	@Subscribe
	public void HandleEvent(final StartSessionEvent event) {
		LOGGER.debug("event=" + event.getClass().getName());
		if (resourceManagementAlgorithm.Allocate(event)) {
			eventBus.Post(new LoadProtocolEvent(event));
		} else {
			eventBus.Post(new StartSessionCompletedEvent(event,
					Result.NORESOURCES));
		}
	}

	@Subscribe
	public void HandleEvent(final StartSessionCompletedEvent event) {
		LOGGER.debug("event=" + event.getClass().getName());
		resourceManagementAlgorithm.Release(event);
	}

	@Subscribe
	public void HandleEvent(final LoadProtocolCompletedEvent event) {
		LOGGER.debug("ENTER: event=" + event.getClass().getName());
		if (event.GetProcessDefinition() != null) {
			LOGGER.info("protocol found name="
					+ event.GetProcessDefinition().getName());
			final StartSessionEvent startSessionEvent = event
					.GetLoadProtocolEvent().GetStartSessionEvent();
			StartWorkflowSessionEvent startWorkflowSessionEvent = new StartWorkflowSessionEvent(
					event, startSessionEvent.GetNameSystem(),
					event.GetProcessDefinition(),
					startSessionEvent.IsSingleStep(),
					startSessionEvent.GetData());
			if (resourceData.AddResourceData(startWorkflowSessionEvent)) {
				eventBus.Post(startWorkflowSessionEvent);
			} else {
				eventBus.Post(new StartSessionCompletedEvent(event,
						Result.ERROR, "No resource data found for system="
								+ startWorkflowSessionEvent.GetSystemName()));
			}
		} else {
			LOGGER.debug("protocol not found");
			eventBus.Post(new StartSessionCompletedEvent(event, Result.ERROR,
					"Protocol not found, name="
							+ event.GetLoadProtocolEvent()
									.GetStartSessionEvent().GetProtocolName()));
		}
		LOGGER.debug("LEAVE: event=" + event.getClass().getName());
	}

}
