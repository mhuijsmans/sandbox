package org.mahu.proto.restapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mahu.proto.restapp.asyntask.AsyncTaskManager;
import org.mahu.proto.restapp.engine.SessionId;
import org.mahu.proto.restapp.engine.SessionIdManager;
import org.mahu.proto.restapp.engine.WorkflowEngine;
import org.mahu.proto.restapp.engine.WorkflowEngineException;
import org.mahu.proto.restapp.engine.impl.WorkflowEngineImpl;
import org.mahu.proto.restapp.model.ProcessAlreadyExistsException;
import org.mahu.proto.restapp.model.ProcessDefinition;
import org.mahu.proto.restapp.model.ProcessDefinitionRepo;
import org.mahu.proto.restapp.model.impl.ProcessBuilderException;
import org.mahu.proto.restapp.model.impl.ProcessDefinitionRepoImpl;
import org.mahu.proto.restapp.service.RemoteService;
import org.mahu.proto.restapp.service.RemoteServiceImpl;

public class WorkflowEngine2Test {

	protected final static Logger logger = LogManager.getLogger(WorkflowEngine2Test.class.getName());

	ProcessDefinition processDefinitionOneTaskProcess = null;
	ProcessDefinition processDefinitionTwoTaskProcess = null;
	ProcessDefinition processDefinitionForkJoinProcess = null;
	ProcessDefinition processDefinitionPausableTaskProcess = null;

	private SessionId id;
	private WorkflowEngine engine;
	private final WorkflowEngine.Listener nullObserver = null;

	private ProcessDefinitionRepo processDefinitionRepo;

	@Before
	public void RunBeforeEveryTest() throws ProcessBuilderException,
			ProcessAlreadyExistsException {
		processDefinitionRepo = new ProcessDefinitionRepoImpl();
		{
			String name = "oneTaskProcess";
			processDefinitionOneTaskProcess = TestHelper
					.createEndTaskProcess(name);
			processDefinitionRepo.addProcess(processDefinitionOneTaskProcess);
		}
		{
			String name = "twoTaskProcess";
			processDefinitionTwoTaskProcess = TestHelper
					.createTwoTasksProcess(name);
			processDefinitionRepo.addProcess(processDefinitionTwoTaskProcess);
		}
		{
			String name = "joinForkProcess";
			processDefinitionForkJoinProcess = TestHelper
					.createForkJoinProcess(name);
			processDefinitionRepo.addProcess(processDefinitionForkJoinProcess);
		}
		{
			String name = "pausableTaskProcess";
			processDefinitionPausableTaskProcess = TestHelper
					.createPausableTaskProcess(name);
			processDefinitionRepo
					.addProcess(processDefinitionPausableTaskProcess);
		}
		engine = new WorkflowEngineImpl();
		id = SessionIdManager.Create();
	}

	@After
	public void RunAfterEveryTest() {
	}

	/**
	 * Test execution of a process with only end-task
	 */
	@Test(timeout = 2000)
	public void execute_oneTaskProcess_success() throws WorkflowEngineException {
		// prepare
		Map<String, Object> data = new HashMap<String, Object>();
		engine.Init(id, processDefinitionOneTaskProcess, data, nullObserver);
		// test
		WorkflowEngine.State state = engine.ExecuteOneJob();
		// verify
		assertEquals(state, WorkflowEngine.State.TERMINATED);
	}

	/**
	 * Test execution of a process with only <task> <endtask>
	 */
	@Test(timeout = 2000)
	public void execute_twoTaskProcess_success() throws WorkflowEngineException {
		// prepare
		Map<String, Object> data = new HashMap<String, Object>();
		engine.Init(id, processDefinitionTwoTaskProcess, data, nullObserver);
		// test
		WorkflowEngine.State state1 = engine.ExecuteOneJob();
		WorkflowEngine.State state2 = engine.ExecuteOneJob();
		// verify
		assertEquals(WorkflowEngine.State.EXECUTING_JOBS_EXIST, state1);
		assertEquals(WorkflowEngine.State.TERMINATED, state2);
	}

	/**
	 * Test execution of a process with fork/join
	 */
	@Test(timeout = 2000)
	public void execute_forkJoinProcess_success()
			throws WorkflowEngineException {
		// prepare
		Map<String, Object> data = new HashMap<String, Object>();
		engine.Init(id, processDefinitionForkJoinProcess, data, nullObserver);
		int nrOfTasksInProcess = 7;
		for (int i = 0; i < nrOfTasksInProcess; i++) {
			logger.info("i=" + i);
			// test
			WorkflowEngine.State state = engine.ExecuteOneJob();
			// verify
			assertEquals(WorkflowEngine.State.EXECUTING_JOBS_EXIST, state);
		}
		// test & verify
		WorkflowEngine.State state = engine.ExecuteOneJob();
		assertEquals(WorkflowEngine.State.TERMINATED, state);
	}

	/**
	 * Test execution of a process with pausable task executed via the
	 * AsyncTaskManagerInterface
	 * 
	 * @throws Exception
	 */
	@Test
	// (timeout = 2000)
	public void execute_pausableTaskProcess_successAndObserverInvoked()
			throws Exception {
		// prepare
		TestListener observer = new TestListener();
		Map<String, Object> data = new HashMap<String, Object>();
		TestAsynctaskManager asynctaskManager = new TestAsynctaskManager();
		data.put(AsyncTaskManager.class.getName(), asynctaskManager);
		data.put(RemoteService.class.getName(), new RemoteServiceImpl());
		engine.Init(id, processDefinitionPausableTaskProcess, data, observer);
		// test & verify
		assertEquals(WorkflowEngine.State.EXECUTING_PAUZED,
				engine.ExecuteOneJob());
		assertEquals(0, observer.cntr);
		asynctaskManager.ExecuteTask();
		assertEquals(1, observer.cntr);
		assertEquals(WorkflowEngine.State.EXECUTING_JOBS_EXIST,
				engine.ExecuteOneJob());
		assertEquals(WorkflowEngine.State.TERMINATED, engine.ExecuteOneJob());
		assertEquals(1, observer.cntr);
	}

	class TestAsynctaskManager implements AsyncTaskManager {
		Callable<Void> task = null;

		@Override
		public void Submit(SessionId id, Callable<Void> task) {
			if (this.task != null) {
				throw new RuntimeException(
						"Already task scheduled. Max one supported");
			}
			this.task = task;
		}

		public void ExecuteTask() throws Exception {
			assertNotNull(task);
			task.call();
			task = null;
		}
	}

	static class TestListener implements WorkflowEngine.Listener {
		int cntr = 0;

		@Override
		public void ProcessTaskFutureHasCompleted(SessionId id) {
			cntr++;
		}

	}

}
