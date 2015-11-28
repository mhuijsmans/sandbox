package org.mahu.proto.restappexp;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mahu.proto.restapp.engine.SessionId;
import org.mahu.proto.restapp.engine.SessionIdManager;
import org.mahu.proto.restapp.model.ProcessDefinition;
import org.mahu.proto.restappext.event.LoadProtocolCompletedEvent;
import org.mahu.proto.restappext.event.LoadProtocolEvent;
import org.mahu.proto.restappext.event.StartSessionCompletedEvent;
import org.mahu.proto.restappext.event.StartSessionCompletedEvent.Result;
import org.mahu.proto.restappext.event.StartSessionEvent;
import org.mahu.proto.restappext.event.StartWorkflowSessionEvent;
import org.mahu.proto.restappext.eventbus.AsyncEventBus;
import org.mahu.proto.restappext.service.ResourceManagementAlgorithm;
import org.mahu.proto.restappext.service.ResourceManagerService;
import org.mockito.Mockito;

public class ResourceManagerTest {

	private SessionId id;
	private final String nameProtocol = new String("test");
	private boolean singleStep = false;

	@Before
	public void RunBeforeEveryTest() {
		id = SessionIdManager.Create();
	}

	@After
	public void RunAfterEveryTest() {
	}

	@Test(timeout = 3000)
	public void postStartSessionEvent_NoResource_startSessionCompletedEventPosted() {
		// Preparation		
		AsyncEventBus eventBusMock = mock(AsyncEventBus.class);
		ResourceManagementAlgorithm rmMock = mock(ResourceManagementAlgorithm.class);
		ResourceManagerService rmService = new ResourceManagerService(
				eventBusMock, rmMock, null);
		StartSessionEvent startSessionEventMock = new StartSessionEvent(id,
				nameProtocol, singleStep);
		when(rmMock.Allocate(startSessionEventMock)).thenReturn(false);
		// test
		rmService.HandleEvent(startSessionEventMock);
		// verify
		StartSessionCompletedEvent startSessionCompletedEvent = new StartSessionCompletedEvent(
				startSessionEventMock, Result.NORESOURCES);
		Mockito.verify(eventBusMock, Mockito.times(1)).Post(
				startSessionCompletedEvent);
	}

	// This test case contains more code than the SOT. 
	@Test(timeout = 3000)
	public void postStartSessionEvent_AllOk_eventsSendAndResourceAllocatedAndReleased() {
		// Preparation
		AsyncEventBus eventBusMock = mock(AsyncEventBus.class);
		ProcessDefinition processDefinitionMock = mock(ProcessDefinition.class);
		ResourceManagementAlgorithm rmMock = mock(ResourceManagementAlgorithm.class);
		ResourceManagerService rmService = new ResourceManagerService(
				eventBusMock, rmMock, null);
		// received events
		StartSessionEvent startSessionEventMock = new StartSessionEvent(id,
				nameProtocol, singleStep);
		when(rmMock.Allocate(startSessionEventMock)).thenReturn(true);
		when(processDefinitionMock.getName()).thenReturn(
				"ProcessDefinition.Name");
		// test
		rmService.HandleEvent(startSessionEventMock);
		//
		LoadProtocolEvent loadProtocolEvent = new LoadProtocolEvent(
				startSessionEventMock);
		LoadProtocolCompletedEvent lpCEventMock = new LoadProtocolCompletedEvent(
				loadProtocolEvent, processDefinitionMock);
		rmService.HandleEvent(lpCEventMock);
		//
		StartWorkflowSessionEvent startWorkflowSessionEventMock = new StartWorkflowSessionEvent(
				lpCEventMock, startSessionEventMock.GetNameSystem(),
				lpCEventMock.GetProcessDefinition(),
				startSessionEventMock.IsSingleStep(), null);
		StartSessionCompletedEvent startSessionCompletedEventMock = new StartSessionCompletedEvent(
				startSessionEventMock, Result.NORESOURCES);
		rmService.HandleEvent(startSessionCompletedEventMock);
		// verify
		// Mockito.verify uses the equals methods of e.g. of the
		// StartSessionEvent class, to verify the argument of a method.
		Mockito.verify(rmMock, Mockito.times(1))
				.Allocate(startSessionEventMock);
		Mockito.verify(eventBusMock, Mockito.times(1)).Post(loadProtocolEvent);
		Mockito.verify(eventBusMock, Mockito.times(1)).Post(
				startWorkflowSessionEventMock);
		Mockito.verify(rmMock, Mockito.times(1)).Release(
				startSessionCompletedEventMock);
	}

}
