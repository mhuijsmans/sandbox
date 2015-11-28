package org.mahu.proto.jnitest;

import static org.junit.Assert.assertTrue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mahu.proto.jnitest.cpptaskexecutor.Task;
import org.mahu.proto.jnitest.cpptaskexecutor.TaskExecutor;
import org.mahu.proto.jnitest.logging.LoggerProxy;
import org.mahu.proto.jnitest.logging.LoggerProxyImpl;
import org.mahu.proto.jnitest.model.ResultData;
import org.mahu.proto.jnitest.model.Settings;

public class ManualCppTaskExecutorTest {
	
	private static Logger LOGGER = LogManager.getLogger(ManualCppTaskExecutorTest.class);	

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@BeforeClass
	public static void init() {
		LOGGER.info(System.getProperty("java.library.path"));
		//
		DynamicLibraryTestUtil.copyDynamicLibraries();
		//
		System.loadLibrary("taskexecutor");
	}

	// To run this test case, update the TaskNoOp
	@Ignore
	@Test
	public void tryIfNoOpCanAccessMethodOfDerivedClass() {
		LoggerProxy log = new LoggerProxyImpl(true);
		TaskExecutor taskExecutor = new TaskExecutor(log);
		try {
			taskExecutor.initCpp();
			Settings settings = new Settings();
			ResultData result = new ResultData();
			// 
			Task task = new NoOpTask(Task.TaskIds.NO_OP_TASK, settings, result);
			taskExecutor.add(task);
			//
			taskExecutor.executeCpp();
			//
			assertTrue("Not all tasks are executed",
					taskExecutor.getNrOfQueuedTasks() == 0);
		} finally {
			taskExecutor.disposeCpp();
		}
	}

	class NoOpTask extends Task {

		public NoOpTask(final TaskIds taskId, final Settings settings,
				final ResultData resultData) {
			super(taskId, settings, resultData);
		}

		public String getNoOpString() {
			return "no-op";
		}

	}

}
