package org.mahu.proto.restappextra;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mahu.proto.forkprocesstest.ProcessManager;
import org.mahu.proto.restapp.asyntask.AsyncTaskManager;
import org.mahu.proto.restapp.engine.SessionId;
import org.mahu.proto.restappext.event.StartupEvent;
import org.mahu.proto.restappext.eventbus.AsyncEventBusImpl;
import org.mahu.proto.restappext.service.AsyncTaskManagerService;
import org.mahu.proto.restappext.service.ProtocolLoaderService;
import org.mahu.proto.restappext.service.RequestService;
import org.mahu.proto.restappext.service.RequestService.RequestResult;
import org.mahu.proto.restappext.service.RequestService.RequestTarget;
import org.mahu.proto.restappext.service.RequestServiceImpl;
import org.mahu.proto.restappext.service.ResourceDataSystems;
import org.mahu.proto.restappext.service.ResourceManagementOneRequestPerSystem;
import org.mahu.proto.restappext.service.ResourceManagerService;
import org.mahu.proto.restappext.service.SingleStepProxy;
import org.mahu.proto.restappext.service.WorkflowService;
import org.mahu.proto.restappextra.config.SystemConfiguration;
import org.mahu.proto.restappextra.config.SystemConfigurationImpl;
import org.mahu.proto.restappextra.remoteservice.FServiceSystem1;
import org.mahu.proto.restappextra.remoteservice.IServiceSystem1;
import org.mahu.proto.restappextra.remoteservice.SServiceSystem1;
import org.mahu.proto.restappextra.remoteservice.VServiceSystem1;
import org.mahu.proto.restappextra.service.RemoteServiceManagerService;
import org.mahu.proto.restappextra.service.SInterface;
import org.mahu.proto.restappextra.service.SProxy;
import org.mahu.proto.restappextra.service.VInterface;
import org.mahu.proto.restappextra.service.VProxy;

public class IntegrationTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	protected final static Logger LOGGER = LogManager.getLogger(IntegrationTest.class.getName());

	private AsyncEventBusImpl mainEventBus;
	private AsyncEventBusImpl workerEventBus;
	private RequestService requestService;
	private ResourceManagementOneRequestPerSystem resourceManagementOneRequestPerSystem;
	private ResourceDataSystems resourceDataSystems;
	// private RemoteServiceManagerService remoteServiceManagerService;
	private ProcessManager processManager;

	// todo/option: register showdown to stop child processes when this process
	// crashes.
	// Runtime.getRuntime().addShutdownHook(new Thread() {
	// @Override
	// public void run() {
	// remoteServiceManagerService.
	// }
	// });

	@Before
	public void runBeforeEveryTest() throws InterruptedException {
		LOGGER.info("ENTER");
		// ================================================================
		// Create the 2 event busses that will be used
		// - mainEventBus executing the protocols, where sequential execution is
		// core.
		// - workerEventBus taking care of any non-trivial task that takes time
		// and that can be executed independent of any other task
		mainEventBus = new AsyncEventBusImpl("TEST");
		workerEventBus = new AsyncEventBusImpl("WORKER", 5);
		// ================================================================
		// Instantiate and configure services that will run on the eventbus
		// RequestService providing a interface for testing
		requestService = new RequestServiceImpl(mainEventBus);
		// ResourceManagerService managing resources and resource data
		resourceManagementOneRequestPerSystem = new ResourceManagementOneRequestPerSystem();
		resourceDataSystems = new ResourceDataSystems();
		ResourceManagerService resourceManagerService = new ResourceManagerService(
				mainEventBus, resourceManagementOneRequestPerSystem,
				resourceDataSystems);
		// ProtocolLoaderService loading protocol
		ProtocolLoaderService protocolLoaderService = new ProtocolLoaderService(
				mainEventBus, workerEventBus);
		// AsyncTaskManagerService executing async jobs created by tasks
		AsyncTaskManagerService asynctaskManagerService = new AsyncTaskManagerService(
				mainEventBus, workerEventBus);
		// SingleStepProxy enabling single stepping through the protocol
		SingleStepProxy singleStepProxy = new SingleStepProxy(workerEventBus);
		// RemoteServiceManagerService starts and stops processes
//		Class<?>[] mainClassesToStart = new Class<?>[] { VServiceSystem1.class,
//				IServiceSystem1.class, FServiceSystem1.class };
//		remoteServiceManagerService = new RemoteServiceManagerService(
//				mainClassesToStart);
		// ================================================================
		// Configure remote systems
		// Data will be used for annotating task and services.
		Map<String, Object> dataSystem1 = new HashMap<>();
		dataSystem1.put(SystemConfiguration.class.getName(),
				new SystemConfigurationImpl(TestConst.SYSTEM1_VPORT,
						TestConst.SYSTEM1_SPORT));
		resourceDataSystems.AddSystemSettings(TestConst.SYSTEM1, dataSystem1);
		// ================================================================
		// Add the annotatable services that are available to tasks.
		WorkflowService workflowService = new WorkflowService(mainEventBus);
		workflowService.AddAnnotatableService(VInterface.class, new VProxy());
		workflowService.AddAnnotatableService(SInterface.class, new SProxy());
		workflowService.AddAnnotatableService(AsyncTaskManager.class,
				asynctaskManagerService);
		// ================================================================
		// Register services running on the main eventbus
		mainEventBus.Register(requestService);
		mainEventBus.Register(resourceManagerService);
		mainEventBus.Register(protocolLoaderService);
		mainEventBus.Register(workflowService);
		mainEventBus.Register(singleStepProxy);
		// mainEventBus.Register(remoteServiceManagerService);
		// ================================================================
		// Register services running on the worker eventbus
		workerEventBus.Register(protocolLoaderService);
		workerEventBus.Register(asynctaskManagerService);
		workerEventBus.Register(singleStepProxy);
		// ================================================================
		// Preparation is done, post the startUp event
		// Current services do not require the startup event
		// mainEventBus.Post(new StartupEvent());
		// ================================================================
		// Start permanent processes / services
		MeetupRestService meetup = new MeetupRestService(4);
		meetup.Start();
		Class<?>[] mainClassesToStart = new Class<?>[] { VServiceSystem1.class,
				IServiceSystem1.class, FServiceSystem1.class, SServiceSystem1.class };
		processManager = new ProcessManager(mainClassesToStart.length);		
		for(Class<?> cls: mainClassesToStart) {
			processManager.StartProcess(cls, true);
		}			
		meetup.WaitForAllParticipants(15);
		LOGGER.info("LEAVE");
	}

	@After
	public void runAfterEveryTest() {
		LOGGER.info("ENTER");
		mainEventBus.StopNow();
		workerEventBus.StopNow();
		processManager.StopProcesses();		
		assertEquals(0, mainEventBus.GetDeadEventSubscriber()
				.GetDeadEventCntr());
		assertEquals(0, workerEventBus.GetDeadEventSubscriber()
				.GetDeadEventCntr());
		// temp solution
		// remoteServiceManagerService.StopProcesses();
		LOGGER.info("LEAVE");
	}

	@Test(timeout = 30000)
	public void startSession_Request_ok() throws InterruptedException {
		LOGGER.info("ENTER");
		// Preparation
		final RequestTarget target = new RequestTarget(TestConst.SYSTEM1,
				TestConst.TEST_SCRIPT);
		// test
		SessionId id1 = requestService.StartSession(target);
		RequestResult rr1 = requestService.WaitForCompletion(id1);
		// verify
		assertEquals(rr1.GetDetails(), RequestResult.Result.TERMINATED,
				rr1.GetResult());
		LOGGER.info("LEAVE");
	}

	@Test(timeout = 60000)
	public void startSession_xRequests_ok() throws InterruptedException {
		LOGGER.info("ENTER");
		// Preparation
		final RequestTarget target = new RequestTarget(TestConst.SYSTEM1,
				TestConst.TEST_SCRIPT);
		// test
		for (int i = 0; i < 20; i++) {
			SessionId id1 = requestService.StartSession(target);
			RequestResult rr1 = requestService.WaitForCompletion(id1);
			// verify
			assertEquals("run=" + i + " details=" + rr1.GetDetails(),
					RequestResult.Result.TERMINATED, rr1.GetResult());
		}
		LOGGER.info("LEAVE");
	}

}
