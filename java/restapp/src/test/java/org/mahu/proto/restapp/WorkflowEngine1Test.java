package org.mahu.proto.restapp;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mahu.proto.restapp.engine.SessionId;
import org.mahu.proto.restapp.engine.SessionIdManager;
import org.mahu.proto.restapp.engine.WorkflowEngine;
import org.mahu.proto.restapp.engine.WorkflowEngine.State;
import org.mahu.proto.restapp.engine.WorkflowEngineException;
import org.mahu.proto.restapp.engine.impl.ProcessNotFoundException;
import org.mahu.proto.restapp.engine.impl.WorkflowEngineImpl;
import org.mahu.proto.restapp.model.ProcessAlreadyExistsException;
import org.mahu.proto.restapp.model.ProcessDefinition;
import org.mahu.proto.restapp.model.ProcessDefinitionRepo;
import org.mahu.proto.restapp.model.ProcessTask;
import org.mahu.proto.restapp.model.impl.ProcessBuilder;
import org.mahu.proto.restapp.model.impl.ProcessBuilderException;
import org.mahu.proto.restapp.model.impl.ProcessDefinitionRepoImpl;
import org.mahu.proto.restapp.model.impl.ProcessPathBuilder;
import org.mahu.proto.restapp.task.EndTask;
import org.mahu.proto.restapp.task.InvalidResultValueTask;
import org.mahu.proto.restapp.task.TestConfigData;
import org.mahu.proto.restapp.task.TestTaskNext;
import org.mahu.proto.restapp.task.TestTaskNextWithAnnotatedClass;
import org.mahu.proto.restapp.task.TestTaskNextWithAnnotatedNamedClass;
import org.mahu.proto.restapp.task.TestTaskOkNok;
import org.mahu.proto.restapp.task.TestTaskPause;
import org.mahu.proto.restapp.task.TestTaskPauseNotifier;
import org.mahu.proto.restapp.task.TestTaskPauseWithFuture;
import org.mahu.proto.restapp.task.TestTaskPauseWithNotifier;
import org.mahu.proto.restapp.task.TestTaskThrowProcessTaskException;
import org.mockito.Mockito;

public class WorkflowEngine1Test {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	private static int MAX_WAIT_IN_MS = 2000;
	private ProcessDefinition processDefinitionOneTaskProcess = null;
	private Map<String, Object> data;

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
		data = new HashMap<>();
		engine = new WorkflowEngineImpl();
		id = SessionIdManager.Create();
	}

	@After
	public void RunAfterEveryTest() {
	}

	@Test
	public void init_sessionIdIsNull_exception()
			throws ProcessNotFoundException {
		// Preparation
		exception.expect(NullPointerException.class);
		// test
		engine.Init(null, processDefinitionOneTaskProcess, data, nullObserver);
	}

	@Test
	public void init_processDefinitionIsNull_exception()
			throws ProcessNotFoundException {
		// Preparation
		exception.expect(NullPointerException.class);
		// test
		engine.Init(id, null, data, null);
	}

	@Test
	public void init_dataIsNull_exception() throws ProcessNotFoundException {
		// Preparation
		exception.expect(NullPointerException.class);
		// test
		engine.Init(id, processDefinitionOneTaskProcess, null, nullObserver);
	}

	public static class UncaughtExceptionTask implements ProcessTask {

		@Override
		public Enum<?> execute() {
			throw new RuntimeException();
		}
	}

	@Test(timeout = 3000)
	public void ExecuteJobsUntilFinalStateReached_UncaughtExceptionInProcessTask_stateAbortedReturned()
			throws WorkflowEngineException, InterruptedException {
		// Preparation
		String name = "processname";
		ProcessBuilder pb = new ProcessBuilder(name);
		pb.firstTask("name", UncaughtExceptionTask.class);
		ProcessDefinition processDefinition = pb.buildProcessDefinition();
		engine.Init(id, processDefinition, data, nullObserver);
		// test
		State state = engine.ExecuteJobsUntilFinalStateReached(MAX_WAIT_IN_MS);
		// verify
		assertEquals(State.ABORTED, state);
	}

	@Test(timeout = 3000)
	public void ExecuteJobsUntilFinalStateReached_ProcessTaskExceptionThrowInProcessTask_stateAbortedReturned()
			throws WorkflowEngineException, InterruptedException {
		// Preparation
		String name = "processname";
		ProcessBuilder pb = new ProcessBuilder(name);
		pb.firstTask("name", TestTaskThrowProcessTaskException.class);
		ProcessDefinition processDefinition = pb.buildProcessDefinition();
		engine.Init(id, processDefinition, data, nullObserver);
		// test
		State state = engine.ExecuteJobsUntilFinalStateReached(MAX_WAIT_IN_MS);
		// verify
		assertEquals(State.ABORTED, state);
	}

	/**
	 * ProcessTask that has no (annotated) results, but for execute returns a
	 * result.
	 */
	public static class InvalidResultInFinalTask implements ProcessTask {
		public enum Result {
			OK, NOK
		}

		@Override
		public Enum<?> execute() throws ProcessTaskException {
			return Result.OK;
		}
	}

	@Test(timeout = 3000)
	public void ExecuteJobsUntilFinalStateReached_errorInvalidResultInFinalTask_stateAbortedReturned()
			throws WorkflowEngineException, InterruptedException {
		// Preparation
		String name = "processname";
		ProcessBuilder pb = new ProcessBuilder(name);
		pb.firstTask("name", InvalidResultInFinalTask.class).isFinal();
		ProcessDefinition processDefinition = pb.buildProcessDefinition();
		engine.Init(id, processDefinition, data, nullObserver);
		// test
		State state = engine.ExecuteJobsUntilFinalStateReached(MAX_WAIT_IN_MS);
		// verify
		assertEquals(State.ABORTED, state);
	}

	@Test(timeout = 3000)
	public void ExecuteJobsUntilFinalStateReached_errorInvalidResultValueTask_stateAbortedReturned()
			throws WorkflowEngineException, InterruptedException {
		// Preparation
		String name = "processname";
		ProcessBuilder pb = new ProcessBuilder(name);
		pb.firstTask("name", InvalidResultValueTask.class)
				.when(TestTaskOkNok.Result.OK).next("done")
				.when(TestTaskOkNok.Result.NOK).next("done");
		pb.addTask("done", EndTask.class);
		ProcessDefinition processDefinition = pb.buildProcessDefinition();
		engine.Init(id, processDefinition, data, nullObserver);
		// test
		State state = engine.ExecuteJobsUntilFinalStateReached(MAX_WAIT_IN_MS);
		// verify
		assertEquals(State.ABORTED, state);
	}

	/**
	 * Test execution of a process with a ProcessTask that has a annotated
	 * ConfigData class
	 * 
	 * @throws InterruptedException
	 */
	@Test(timeout = 3000)
	public void ExecuteJobsUntilFinalStateReached_errorAnnotatingWrongClass_stateAbortedReturned()
			throws WorkflowEngineException, InterruptedException {
		// Preparation
		String name = "simpleProcess";
		ProcessBuilder pb = new ProcessBuilder(name);
		pb.firstTask("name", TestTaskNextWithAnnotatedClass.class);
		ProcessDefinition processDefinition = pb.buildProcessDefinition();
		engine.Init(id, processDefinition, data, nullObserver);
		// test
		State state = engine.ExecuteJobsUntilFinalStateReached(MAX_WAIT_IN_MS);
		// verify
		assertEquals(State.ABORTED, state);
	}

	/**
	 * Test execution of a process with a ProcessTask that has a annotated Named
	 * ConfigData class, but it has the wrong name.
	 * 
	 * @throws InterruptedException
	 */
	@Test(timeout = 3000)
	public void ExecuteJobsUntilFinalStateReached_errorAnnotatingWrongNamed_stateAbortedReturned()
			throws WorkflowEngineException, InterruptedException {
		// Preparation
		String name = "simpleProcess";
		ProcessBuilder pb = new ProcessBuilder(name);
		pb.firstTask("name", TestTaskNextWithAnnotatedNamedClass.class);
		ProcessDefinition processDefinition = pb.buildProcessDefinition();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put(TestTaskNextWithAnnotatedNamedClass.NAMED_VALUE + "1",
				new TestConfigData());
		engine.Init(id, processDefinition, data, nullObserver);
		// test
		State state = engine.ExecuteJobsUntilFinalStateReached(MAX_WAIT_IN_MS);
		// verify
		assertEquals(State.ABORTED, state);
	}

	/**
	 * Test execution of a process with a ProcessTask that has a annotated Named
	 * ConfigData class, but it is not found.
	 * 
	 * @throws InterruptedException
	 */
	@Test(timeout = 3000)
	public void ExecuteJobsUntilFinalStateReached_errorAnnotatingAbsentNamed_stateAbortedReturned()
			throws WorkflowEngineException, InterruptedException {
		// Preparation
		String name = "simpleProcess";
		ProcessBuilder pb = new ProcessBuilder(name);
		pb.firstTask("name", TestTaskNextWithAnnotatedNamedClass.class);
		ProcessDefinition processDefinition = pb.buildProcessDefinition();
		engine.Init(id, processDefinition, data, nullObserver);
		// test
		State state = engine.ExecuteJobsUntilFinalStateReached(MAX_WAIT_IN_MS);
		// verify
		assertEquals(State.ABORTED, state);
	}

	@Test(timeout = 3000)
	public void ExecuteJobsUntilFinalStateReached_emptyProcess_stateTerminatedReturned()
			throws WorkflowEngineException, InterruptedException {
		// Preparation
		String name = "emptyProcess";
		ProcessDefinition processDefinition = TestHelper
				.createEndTaskProcess(name);
		engine.Init(id, processDefinition, data, nullObserver);
		// test
		State state = engine.ExecuteJobsUntilFinalStateReached(MAX_WAIT_IN_MS);
		// verify
		assertEquals(State.TERMINATED, state);
	}

	/**
	 * Test execution of a process with only an EndTask
	 * 
	 * @throws InterruptedException
	 */
	@Test(timeout = 3000)
	public void ExecuteJobsUntilFinalStateReached_endTaskProcess_stateTerminatedReturned()
			throws WorkflowEngineException, InterruptedException {
		// Preparation
		String name = "simpleProcess";
		ProcessBuilder pb = new ProcessBuilder(name);
		pb.firstTask("name", EndTask.class);
		ProcessDefinition processDefinition = pb.buildProcessDefinition();
		engine.Init(id, processDefinition, data, nullObserver);
		// test
		State state = engine.ExecuteJobsUntilFinalStateReached(MAX_WAIT_IN_MS);
		// verify
		assertEquals(State.TERMINATED, state);
	}

	/**
	 * Test execution of a process with a ProcessTask that has a annotated
	 * ConfigData class
	 * 
	 * @throws InterruptedException
	 */
	@Test(timeout = 3000)
	public void ExecuteJobsUntilFinalStateReached_processTaskWithAnnotatedClass_stateTerminatedReturned()
			throws WorkflowEngineException, InterruptedException {
		// Preparation
		String name = "simpleProcess";
		ProcessBuilder pb = new ProcessBuilder(name);
		pb.firstTask("name", TestTaskNextWithAnnotatedClass.class);
		ProcessDefinition processDefinition = pb.buildProcessDefinition();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put(TestConfigData.class.getName(), new TestConfigData());
		engine.Init(id, processDefinition, data, nullObserver);
		// test
		State state = engine.ExecuteJobsUntilFinalStateReached(MAX_WAIT_IN_MS);
		// verify
		assertEquals(State.TERMINATED, state);
	}

	/**
	 * Test execution of a process with a ProcessTask that has a annotated &
	 * named ConfigData class
	 * 
	 * @throws InterruptedException
	 */
	@Test(timeout = 3000)
	public void ExecuteJobsUntilFinalStateReached_processTaskWithAnnotatedNamedClass_stateTerminatedReturned()
			throws WorkflowEngineException, InterruptedException {
		// Preparation
		String name = "simpleProcess";
		ProcessBuilder pb = new ProcessBuilder(name);
		pb.firstTask("name", TestTaskNextWithAnnotatedNamedClass.class);
		ProcessDefinition processDefinition = pb.buildProcessDefinition();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put(TestTaskNextWithAnnotatedNamedClass.NAMED_VALUE,
				new TestConfigData());
		engine.Init(id, processDefinition, data, nullObserver);
		// test
		State state = engine.ExecuteJobsUntilFinalStateReached(MAX_WAIT_IN_MS);
		// verify
		assertEquals(State.TERMINATED, state);
	}

	/**
	 * Test execution of a basic process
	 * 
	 * @throws InterruptedException
	 */
	@Test(timeout = 3000)
	public void ExecuteJobsUntilFinalStateReached_basicProcess_stateTerminatedReturned()
			throws WorkflowEngineException, InterruptedException {
		// Preparation
		String name = "simpleProcess";
		ProcessBuilder pb = new ProcessBuilder(name);
		pb.firstTask("name", TestTaskOkNok.class) //
				.when(TestTaskOkNok.Result.OK).next("done") //
				.when(TestTaskOkNok.Result.NOK).next("done");
		pb.addTask("done", EndTask.class);
		ProcessDefinition processDefinition = pb.buildProcessDefinition();
		engine.Init(id, processDefinition, data, null);
		// test
		State state = engine.ExecuteJobsUntilFinalStateReached(MAX_WAIT_IN_MS);
		// verify
		assertEquals(State.TERMINATED, state);
	}

	/**
	 * Test execution of a basic process, single task that implements
	 * PausibleTask (probably deprecated)
	 * 
	 * @throws InterruptedException
	 */
	@Test(timeout = 3000)
	public void ExecuteJobsUntilFinalStateReached_basicProcessWithPausedTask_stateTerminatedReturned()
			throws WorkflowEngineException, InterruptedException {
		// Preparation
		String name = "simpleProcess";
		ProcessBuilder pb = new ProcessBuilder(name);
		pb.firstTask("pausedAndContinue", TestTaskPause.class) //
				.when(ProcessTask.Result.Next).next("done");
		pb.addTask("done", EndTask.class);
		ProcessDefinition processDefinition = pb.buildProcessDefinition();
		engine.Init(id, processDefinition, data, nullObserver);
		// test
		State state = engine.ExecuteJobsUntilFinalStateReached(MAX_WAIT_IN_MS);
		// verify
		assertEquals(State.TERMINATED, state);
	}

	/**
	 * Test execution of a basic process, single task that implements
	 * PausableTask and uses Future
	 * 
	 * @throws InterruptedException
	 */
	@Test(timeout = 3000)
	public void ExecuteJobsUntilFinalStateReached_processWithPausedTaskUsingFuture_stateTerminatedReturned()
			throws WorkflowEngineException, InterruptedException {
		// Preparation
		String name = "simpleProcess";
		ProcessBuilder pb = new ProcessBuilder(name);
		pb.firstTask("pausedAndContinue", TestTaskPauseWithFuture.class) //
				.when(ProcessTask.Result.Next).next("done");
		pb.addTask("done", EndTask.class);
		ProcessDefinition processDefinition = pb.buildProcessDefinition();
		engine.Init(id, processDefinition, data, nullObserver);
		// test
		State state = engine.ExecuteJobsUntilFinalStateReached(MAX_WAIT_IN_MS);
		// verify
		assertEquals(State.TERMINATED, state);
	}

	/**
	 * Test execution of a basic process with fork/join
	 * 
	 * @throws InterruptedException
	 */
	@Test(timeout = 3000)
	public void ExecuteJobsUntilFinalStateReached_basicForkJoinProcess_stateTerminatedReturned()
			throws WorkflowEngineException, InterruptedException {
		// Preparation
		String name = "simpleProcess";
		ProcessBuilder pb = new ProcessBuilder(name);
		pb.firstTask("first", TestTaskNext.class) //
				.when(ProcessTask.Result.Next).next("fork");
		ProcessPathBuilder path1 = pb.createProcessPath("path1");
		ProcessPathBuilder path2 = pb.createProcessPath("path2");
		pb.addFork("fork", new ProcessPathBuilder[] { path1, path2 })
				.withJoin("join").next("last");
		path1.firstTask("1.1", TestTaskNext.class) //
				.when(ProcessTask.Result.Next).next("join");
		path2.firstTask("2.1", TestTaskNext.class) //
				.when(ProcessTask.Result.Next).next("join");
		pb.addTask("last", EndTask.class);
		ProcessDefinition processDefinition = pb.buildProcessDefinition();
		engine.Init(id, processDefinition, data, nullObserver);
		// test
		State state = engine.ExecuteJobsUntilFinalStateReached(MAX_WAIT_IN_MS);
		// verify
		assertEquals(State.TERMINATED, state);
	}

	/**
	 * Test execution of a basic process with fork/join, with a paused task and
	 * exception taks. The exception task results in canceling of the paused
	 * task.
	 * 
	 * @throws InterruptedException
	 */
	@Test(timeout = 3000)
	public void ExecuteJobsUntilFinalStateReached_basicForkJoinProcessPauzeCancelledBecauseOfExceptionInOtherTasks_stateTerminatedAborted()
			throws WorkflowEngineException, InterruptedException {
		// Preparation
		String name = "simpleProcess";
		ProcessBuilder pb = new ProcessBuilder(name);
		pb.firstTask("first", TestTaskNext.class) //
				.when(ProcessTask.Result.Next).next("fork");
		ProcessPathBuilder path1 = pb.createProcessPath("path1");
		ProcessPathBuilder path2 = pb.createProcessPath("path2");
		pb.addFork("fork", new ProcessPathBuilder[] { path1, path2 })
				.withJoin("join").next("last");
		path1.firstTask("1.1", TestTaskPauseWithNotifier.class) //
				.when(ProcessTask.Result.Next).next("join");
		path2.firstTask("2.1", TestTaskThrowProcessTaskException.class) //
				.when(ProcessTask.Result.Next).next("join");
		pb.addTask("last", EndTask.class);
		ProcessDefinition processDefinition = pb.buildProcessDefinition();
		//
		Map<String, Object> data = new HashMap<String, Object>();
		TestTaskPauseNotifier notifier = Mockito
				.mock(TestTaskPauseNotifier.class);
		data.put(TestTaskPauseNotifier.class.getName(), notifier);
		engine.Init(id, processDefinition, data, nullObserver);
		// test
		State state = engine.ExecuteJobsUntilFinalStateReached(MAX_WAIT_IN_MS);
		// verify
		Mockito.verify(notifier).cancelled();
		assertEquals(State.ABORTED, state);
	}

	/**
	 * Test execution of a basic process with fork/join where nested fork/join.
	 * 
	 * @throws InterruptedException
	 */
	@Test(timeout = 3000)
	public void ExecuteJobsUntilFinalStateReached_basicNestedForkJoinProcess_stateTerminatedTerminated()
			throws WorkflowEngineException, InterruptedException {
		// Preparation
		String name = "simpleProcess";
		ProcessBuilder pb = new ProcessBuilder(name);
		pb.firstTask("first", TestTaskNext.class) //
				.when(ProcessTask.Result.Next).next("fork");
		ProcessPathBuilder path1 = pb.createProcessPath("path1");
		path1.firstTask("1.1", TestTaskPause.class) //
				.when(ProcessTask.Result.Next).next("join");
		//
		ProcessPathBuilder path2 = pb.createProcessPath("path2");
		//
		ProcessPathBuilder path3 = path2.createProcessPath("path3");
		path3.firstTask("2.1.1", TestTaskPause.class) //
				.when(ProcessTask.Result.Next).next("join2.1");
		//
		ProcessPathBuilder path4 = path2.createProcessPath("path4");
		path4.firstTask("2.1.2", TestTaskNext.class) //
				.when(ProcessTask.Result.Next).next("join2.1");
		//
		path2.firstTask("2.1", TestTaskNext.class) //
				.when(ProcessTask.Result.Next).next("fork2.1");
		path2.addFork("fork2.1", new ProcessPathBuilder[] { path3, path4 })
				.withJoin("join2.1").next("join");
		//
		pb.addFork("fork", new ProcessPathBuilder[] { path1, path2 })
				.withJoin("join").next("last");
		pb.addTask("last", EndTask.class);
		//
		ProcessDefinition processDefinition = pb.buildProcessDefinition();
		engine.Init(id, processDefinition, data, nullObserver);
		// test
		State state = engine.ExecuteJobsUntilFinalStateReached(MAX_WAIT_IN_MS);
		// verify
		assertEquals(State.TERMINATED, state);
	}
}
