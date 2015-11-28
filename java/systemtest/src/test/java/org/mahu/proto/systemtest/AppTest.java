package org.mahu.proto.systemtest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.junit.Test;
import org.mahu.proto.forkprocesstest.ChildProcess;
import org.mahu.proto.jerseyjunittools.RestResource;
import org.mahu.proto.systemtest.logging.Logging;
import org.mahu.proto.systemtest.operatingsystem.OperatingSystem;
import org.mahu.proto.systemtest.persub.PresenceListener;

public class AppTest {
	static {
		// This must be the first statement, so logging is in place before it is
		// used.
		Logging.setLoggingProperties();
	}

	private static final Logger log = Logger.getLogger(AppTest.class.getName());

	@Test
	public void testProcessReturnsOneLine() throws InterruptedException,
			ExecutionException {
		ExecutorService executor = Executors.newFixedThreadPool(6);
		//
		PresenceListener presenceListener = new PresenceListener();
		FutureTask<Void> presenceListenerFuture = new FutureTask<Void>(
				presenceListener);
		executor.execute(presenceListenerFuture);
		executor.execute(new FutureTask<Void>(presenceListener
				.getPresenceReporter()));
		//
		ChildProcess child = new ChildProcess();
		child.printChildDataToConsole();
		FutureTask<Integer> futureTask = child
				.createTaskCloneOwnProcess(OperatingSystem.class);
		executor.execute(futureTask);
		//
		FutureTask<Void> closeStreamTask = new FutureTask<Void>(
				new TaskCloseInputStreamAfterShortDelay(child));
		executor.execute(closeStreamTask);
		//
		TimeUnit.SECONDS .sleep(1);
		startCallToRunApplications();
		//

		int exitValue = futureTask.get().intValue();
		printOutputData(child);
		assertEquals(0, exitValue);
		TimeUnit.SECONDS.sleep(5);
	}

	private void startCallToRunApplications() {
		RestResource<String> resource = RestUtils.startCallToRunApplications();
		assertEquals(200, resource.getResponseCode());
	}

	protected static void printOutputData(ChildProcess fork) {
		print("OutputData", fork.getOutputData());
		print("ErrorData", fork.getErrorData());
	}

	private static void print(final String msg, final List<String> lines) {
		System.out.println(msg);
		for (String s : lines) {
			System.out.println(s);
		}
	}

	class TaskCloseInputStreamAfterShortDelay implements Callable<Void> {
		private final ChildProcess child;

		TaskCloseInputStreamAfterShortDelay(final ChildProcess child) {
			this.child = child;
		}

		@Override
		public Void call() {
			try {
				TimeUnit.SECONDS.sleep(10);
				assertTrue(child.isProcessesRunning());
				//
				log.info("Caling close on steam to OperatingSystem");
				child.closeStreamToChild();
				TimeUnit.SECONDS.sleep(3);
				assertFalse(child.isProcessesRunning());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}
}
