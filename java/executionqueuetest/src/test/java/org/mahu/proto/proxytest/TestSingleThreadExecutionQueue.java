package org.mahu.proto.proxytest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mahu.proto.executionqueuetest.SingleThreadExecutionQueue;

public class TestSingleThreadExecutionQueue {

	private static final Logger log = Logger.getLogger(TestSingleThreadExecutionQueue.class.getName());

	private SingleThreadExecutionQueue queue;

	@Before
	public void beforeTest() {
		queue = new SingleThreadExecutionQueue("one");
	}

	@After
	public void afterTest() {
		if (queue != null) {
			queue.shutdown();
		}
	}

	@Test(timeout = 1000)
	public void testExecuteOneTask() throws InterruptedException {
		// preparation
		TestRunner1 testTask = new TestRunner1();
		// test
		queue.execute(testTask);
		testTask.waitForExecuted();
	}

	@Test(timeout = 1000)
	public void testExecuteOneTaskWithListener() throws InterruptedException {
		// preparation
		QueueListener listener = new QueueListener(1);
		queue.setListener(listener);
		// test
		queue.execute(new TestRunner1());
		listener.waitForExecuted();
	}

	@Test(timeout = 1000)
	public void testExecuteManyTasks() throws InterruptedException {
		// preparation
		int nrOfTasks = 30;
		QueueListener listener = new QueueListener(30);
		queue.setListener(listener);
		// test
		for (int i = 0; i < nrOfTasks; i++) {
			queue.execute(new TestRunner1());
		}
		listener.waitForExecuted();
	}
	
	@Test(timeout = 10000)
	public void testForcedShutdown() throws InterruptedException {
		// preparation
		TestRunnerSleep7Sec task = new TestRunnerSleep7Sec();
		queue.execute(task);
		task.waitForStarted();
		// The task is sleeping and will prevent graceful shutdown
		// test. So forced shutdown which will interrupt the task
		queue.shutdown();
		task.waitForInterrupted();
	}	

	class TestRunner1 implements Runnable {
		private CountDownLatch cdl = new CountDownLatch(1);

		@Override
		public void run() {
			cdl.countDown();
			log.info("Hi");
		}

		public void waitForExecuted() throws InterruptedException {
			cdl.await();
		}
	}
	
	class TestRunnerSleep7Sec implements Runnable {
		private CountDownLatch cdl = new CountDownLatch(1);
		private CountDownLatch interrupted =  new CountDownLatch(1);
		@Override
		public void run() {
			cdl.countDown();
			try {
				TimeUnit.SECONDS.sleep(SingleThreadExecutionQueue.SHUTDOWN_TIMEOUT_IN_SECONDS+2);
			} catch (InterruptedException e) {
				log.info("Expectedly Interrupted because of hard shutdown");
				interrupted.countDown();
			}
		}
		
		public void waitForStarted() throws InterruptedException {
			cdl.await();
			log.info("waitForStarted returns");
		}	
		
		public void waitForInterrupted() throws InterruptedException {
			interrupted.await();
		}		
	}	

	class QueueListener implements SingleThreadExecutionQueue.ExecutionListener {

		private final CountDownLatch cdl;

		QueueListener() {
			this(1);
		}

		QueueListener(final int nrNrOfTasks) {
			cdl = new CountDownLatch(nrNrOfTasks);
		}

		@Override
		public void preExecute(Runnable command) {
			// nothing
		}

		@Override
		public void postExecute(Runnable command) {
			cdl.countDown();
		}

		public void waitForExecuted() throws InterruptedException {
			cdl.await();
		}

	}
}
