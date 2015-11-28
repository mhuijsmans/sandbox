package org.mahu.proto.restappexp;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mahu.proto.restapp.asyntask.AsyncTaskManager;
import org.mahu.proto.restapp.engine.ProcessTaskFuture;
import org.mahu.proto.restapp.engine.SessionId;
import org.mahu.proto.restappexp.service.ExceptionInAsyncTaskImpl;
import org.mahu.proto.restappexp.service.ExceptionInAsyncTaskInterface;
import org.mahu.proto.restappexp.service.VEInterface;
import org.mahu.proto.restappext.event.SingleStepListener;
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

public class IntegrationTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	protected final static Logger LOGGER = LogManager.getLogger(IntegrationTest.class.getName());

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
		//
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
		resourceDataSystems.AddSystemSettings(TestConst.LOCAL_SYSTEM,
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

	@Test(timeout = 3000)
	public void startSession_joinForkWithPausableTasks_success()
			throws InterruptedException {
		LOGGER.info("ENTER");
		// test
		SessionId id = requestService.StartSession(new RequestTarget(
				TestConst.TEST_SCRIPT));
		// verify
		RequestService.RequestResult rr = requestService.WaitForCompletion(id);
		assertEquals(RequestResult.Result.TERMINATED, rr.GetResult());
		LOGGER.info("LEAVE");
	}

	@Test(timeout = 5000)
	public void startSession_multipleInvokesSequential_success()
			throws InterruptedException {
		LOGGER.info("ENTER");
		for (int i = 0; i < 10; i++) {
			// test
			SessionId id = requestService.StartSession(new RequestTarget(
					TestConst.TEST_SCRIPT));
			// verify
			RequestService.RequestResult rr = requestService
					.WaitForCompletion(id);
			LOGGER.info("checking result of loop=" + i);
			assertEquals("iteration i=" + i + " details=" + rr.GetDetails(),
					RequestResult.Result.TERMINATED, rr.GetResult());
		}
		LOGGER.info("LEAVE");
	}

	@Test(timeout = 5000)
	public void startSession_multipleInvokesParallel_success()
			throws InterruptedException {
		LOGGER.info("ENTER");
		List<SessionId> requests = new LinkedList<>();
		// test
		final int maxRequest = 10;
		for (int i = 0; i < maxRequest; i++) {
			SessionId id = requestService.StartSession(new RequestTarget(
					TestConst.TEST_SCRIPT));
			requests.add(id);
		}
		for (SessionId id : requests) {
			// verify
			requestService.WaitForCompletion(id);
		}

		LOGGER.info("LEAVE");
	}

	@Test(timeout = 3000)
	public void startSessionWithSingleStep_joinForkNoPausableTasks_success()
			throws InterruptedException {
		LOGGER.info("ENTER");
		// test
		ProcessListener listener = new ProcessListener();
		SessionId id = requestService.StartSessionWithSingleStep(
				new RequestTarget(TestConst.TEST_SCRIPT), listener);
		while (true) {
			listener.WaitForEvent();
			requestService.Step(id);
			if (listener.IsSessionCompleted()) {
				break;
			}
		}
		// verify
		LOGGER.info("LEAVE");
	}

	class ProcessListener implements SingleStepListener {
		int cntr = 0;
		private boolean IsSessionCompleted = false;

		@Override
		public void ReadyToStep() {
			DecrCounterAndNotify();
		}

		public synchronized void WaitForEvent() throws InterruptedException {
			cntr++;
			if (cntr > 0) {
				wait();
			}
		}

		@Override
		public synchronized void SessionCompleted() {
			DecrCounterAndNotify();
			IsSessionCompleted = true;
		}

		public synchronized boolean IsSessionCompleted() {
			return IsSessionCompleted;
		}

		private synchronized void DecrCounterAndNotify() {
			LOGGER.info("ProcessListener.DecrCounterAndNotify()");
			cntr--;
			if (cntr == 0) {
				notify();
			}
		}

	}

	@Test(timeout = 3000)
	public void startSession_unknownService_success()
			throws InterruptedException {
		LOGGER.info("ENTER");
		// test
		SessionId id = requestService.StartSession(new RequestTarget(
				"unknown.xml"));
		// verify
		requestService.WaitForCompletion(id);
		LOGGER.info("LEAVE");
	}

	static class VeInterfaceMock implements VEInterface {

		final static Logger logger = LogManager.getLogger(VeInterfaceMock.class.getName());

		@Inject
		SessionId id;

		@Inject
		AsyncTaskManager asyncTaskManager;

		@Inject
		private ProcessTaskFuture future;

		static class AsyncTask implements Callable<Void> {
			private final ProcessTaskFuture future;
			private final String name;

			AsyncTask(final String name, final ProcessTaskFuture future) {
				this.future = future;
				this.name = name;
			}

			@Override
			public Void call() {
				logger.debug(name + " call() ENTER");
				future.AsyncTaskHasCompleted();
				logger.debug(name + " call() LEAVE");
				return null;
			}
		}

		@Override
		public void DoLD() {
			logger.info("DoLD() ENTER");
			asyncTaskManager.Submit(future.GetSessionId(), new AsyncTask(
					GetId(), future));
			logger.info("DoLD() LEAVE");
		}

		@Override
		public void DoBC() {
			logger.info("DoBC() ENTER");
			asyncTaskManager.Submit(future.GetSessionId(), new AsyncTask(
					GetId(), future));
			logger.info("DoBC() LEAVE");
		}

		@Override
		public void DoED() {
			logger.info("DoED() ENTER");
			asyncTaskManager.Submit(future.GetSessionId(), new AsyncTask(
					GetId(), future));
			logger.info("DoED() LEAVE");
		}

		@Override
		public void DoSED() {
			logger.info("DoSED() ENTER");
			asyncTaskManager.Submit(future.GetSessionId(), new AsyncTask(
					GetId(), future));
			logger.info("DoSED() LEAVE");
		}

		private String GetId() {
			return id.toString();
		}

	}

	@Test(timeout = 3000)
	public void startSession_exceptionInTask_abort()
			throws InterruptedException {
		LOGGER.info("ENTER");
		SessionId id = requestService.StartSession(new RequestTarget(
				"exceptiontask.bpmn20.xml"));
		RequestService.RequestResult rr = requestService.WaitForCompletion(id);
		// verify
		assertEquals(RequestResult.Result.ABORTED, rr.GetResult());
		LOGGER.info("LEAVE");
	}

	@Test(timeout = 3000)
	public void post_exceptionInAsyncTask_abort() throws InterruptedException {
		LOGGER.info("ENTER");
		Map<String, Object> data = new HashMap<>();
		data.put(ExceptionInAsyncTaskInterface.class.getName(),
				new ExceptionInAsyncTaskImpl());
		SessionId id = requestService.StartSession(new RequestTarget(
				"exceptionasynctask.bpmn20.xml"), data);
		RequestService.RequestResult rr = requestService.WaitForCompletion(id);
		// verify
		assertEquals(RequestResult.Result.ABORTED, rr.GetResult());
		LOGGER.info("LEAVE");
	}
}
