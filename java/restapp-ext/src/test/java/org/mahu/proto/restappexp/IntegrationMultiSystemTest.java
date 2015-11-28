package org.mahu.proto.restappexp;

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
import org.mahu.proto.restapp.asyntask.AsyncTaskManager;
import org.mahu.proto.restapp.engine.SessionId;
import org.mahu.proto.restappexp.IntegrationTest.VeInterfaceMock;
import org.mahu.proto.restappexp.service.VEInterface;
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

public class IntegrationMultiSystemTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	protected final static Logger LOGGER = LogManager.getLogger(IntegrationMultiSystemTest.class.getName());

	private AsyncEventBusImpl mainEventBus;
	private AsyncEventBusImpl workerEventBus;
	private RequestService requestService;
	private ResourceManagementOneRequestPerSystem resourceManagementOneRequestPerSystem;
	private ResourceDataSystems resourceDataSystems;

	@Before
	public void runBeforeEveryTest() {
		LOGGER.info("ENTER");
		mainEventBus = new AsyncEventBusImpl("TEST");
		workerEventBus = new AsyncEventBusImpl("WORKER", 5);
		// services
		requestService = new RequestServiceImpl(mainEventBus);
		resourceManagementOneRequestPerSystem = new ResourceManagementOneRequestPerSystem();
		resourceDataSystems = new ResourceDataSystems();
		ResourceManagerService resourceManagerService = new ResourceManagerService(
				mainEventBus, resourceManagementOneRequestPerSystem,
				resourceDataSystems);
		// ================================================================
		// Configure remote systems
		// Data will be used for annotating task and services.
		Map<String, Object> dataSystemLocal = new HashMap<>();
		resourceDataSystems.AddSystemSettings(TestConst.SYSTEM1,
				dataSystemLocal);
		//
		ProtocolLoaderService protocolLoaderService = new ProtocolLoaderService(
				mainEventBus, workerEventBus);
		AsyncTaskManagerService asynctaskManagerService = new AsyncTaskManagerService(
				mainEventBus, workerEventBus);
		SingleStepProxy singleStepProxy = new SingleStepProxy(workerEventBus);
		//
		WorkflowService workflowService = new WorkflowService(mainEventBus);
		workflowService.AddAnnotatableService(VEInterface.class,
				new VeInterfaceMock());
		workflowService.AddAnnotatableService(AsyncTaskManager.class,
				asynctaskManagerService);
		//
		mainEventBus.Register(requestService);
		mainEventBus.Register(resourceManagerService);
		mainEventBus.Register(protocolLoaderService);
		mainEventBus.Register(workflowService);
		mainEventBus.Register(singleStepProxy);
		//
		workerEventBus.Register(protocolLoaderService);
		workerEventBus.Register(asynctaskManagerService);
		workerEventBus.Register(singleStepProxy);
		LOGGER.info("LEAVE");
	}

	@After
	public void runAfterEveryTest() {
		LOGGER.info("ENTER");
		mainEventBus.StopNow();
		workerEventBus.StopNow();
		assertEquals(0, mainEventBus.GetDeadEventSubscriber()
				.GetDeadEventCntr());
		assertEquals(0, workerEventBus.GetDeadEventSubscriber()
				.GetDeadEventCntr());
		LOGGER.info("LEAVE");
	}

	@Test(timeout = 5000)
	public void startSession_twoRequestForOneSystem_firstRequestExecutedSecondNot()
			throws InterruptedException {
		LOGGER.info("ENTER");
		// Preparation
		final RequestTarget target = new RequestTarget("system1",
				TestConst.TEST_SCRIPT);
		// test
		SessionId id1 = requestService.StartSession(target);
		SessionId id2 = requestService.StartSession(target);
		RequestResult rr1 = requestService.WaitForCompletion(id1);
		RequestResult rr2 = requestService.WaitForCompletion(id2);
		// verify
		assertEquals("Details="+rr1.GetDetails(), RequestResult.Result.TERMINATED, rr1.GetResult());
		assertEquals("Details="+rr2.GetDetails(), RequestResult.Result.NORESOURCES, rr2.GetResult());
		LOGGER.info("LEAVE");
	}

}
