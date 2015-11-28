package org.mahu.proto.jnitest;

import static org.junit.Assert.assertTrue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mahu.proto.jnitest.cpptaskexecutor.Task;
import org.mahu.proto.jnitest.cpptaskexecutor.TaskExecutor;
import org.mahu.proto.jnitest.logging.LoggerProxy;
import org.mahu.proto.jnitest.logging.LoggerProxyImpl;
import org.mahu.proto.jnitest.model.ResultData;
import org.mahu.proto.jnitest.model.Settings;

public class CppTaskExecutorTest {
	
	private static Logger LOGGER = LogManager.getLogger(CppTaskExecutorTest.class);	

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

	@Test
	public void basicFlowExecution() {
		LoggerProxy log = new LoggerProxyImpl();
		TaskExecutor taskExecutor = new TaskExecutor(log);
		try {
			taskExecutor.initCpp();
			assertTrue(taskExecutor.pointerCppTaskExecutor != 0);
			taskExecutor.executeCpp();
		} finally {
			taskExecutor.disposeCpp();
			assertTrue(taskExecutor.pointerCppTaskExecutor == 0);
		}
	}
	
	@Test
	public void executeOneTask() {
		LoggerProxy log = new LoggerProxyImpl();
		TaskExecutor taskExecutor = new TaskExecutor(log);
		try {
			taskExecutor.initCpp();
			Settings settings = new Settings();
			ResultData result = new ResultData();
			// in
			// Task task = new NoOpTask(Task.TaskIds.NO_OP_TASK, settings, result);
			Task task = new Task(Task.TaskIds.NO_OP_TASK, settings, result);
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

	
}
