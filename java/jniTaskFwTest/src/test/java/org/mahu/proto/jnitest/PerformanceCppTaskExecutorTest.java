package org.mahu.proto.jnitest;

import static org.junit.Assert.assertTrue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mahu.proto.commons.Chrono;
import org.mahu.proto.jnitest.cpptaskexecutor.TaskExecutor;
import org.mahu.proto.jnitest.logging.LoggerProxy;
import org.mahu.proto.jnitest.logging.LoggerProxyImpl;

public class PerformanceCppTaskExecutorTest {

	private static Logger LOGGER = LogManager
			.getLogger(PerformanceCppTaskExecutorTest.class);

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
		LoggerProxy log = new LoggerProxyImpl(false);
		TaskExecutor taskExecutor = new TaskExecutor(log);
		Chrono chrono = new Chrono();
		int max = 100000;
		for (int i = 0; i < max; i++) {
			try {
				taskExecutor.initCpp();
				assertTrue(taskExecutor.pointerCppTaskExecutor != 0);
				taskExecutor.executeCpp();
			} finally {
				taskExecutor.disposeCpp();
				assertTrue(taskExecutor.pointerCppTaskExecutor == 0);
			}
		}
		long elaspedNs = chrono.elapsedMs()*1000;
		long avg = elaspedNs/max;
		System.out.println("avg(ns): "+avg);
	}

}
