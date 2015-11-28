package org.mahu.proto.forkprocesstest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class CloneOwnProcessTest extends ProcessTestBase {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Test
	public void closeOwnProcess_processReturnsOneLine_verify() {
		// preparation
		// test
		ChildProcess fork = new ChildProcess();
		int exitValue = fork.cloneOwnProcess(TestMainClass1.class);
		assertEquals(0, exitValue);
		assertOutputData(fork, TestMainClass1.HI);
		assertEquals(0, fork.getErrorData().size());
		assertFalse(fork.IsProcessesRunning());
	}

	@Test
	public void testProcessReturnsThreeLines() {
		// preparation
		// test
		ChildProcess fork = new ChildProcess();
		int exitValue = fork.cloneOwnProcess(TestMainClass2.class);
		assertEquals(0, exitValue);
		assertOutputData(fork, TestMainClass2.HI);
		assertEquals(0, fork.getErrorData().size());
		assertFalse(fork.IsProcessesRunning());
	}

	@Test
	public void testProcessReturnsThreeLinesToConsole() {
		// preparation
		// test
		ChildProcess fork = new ChildProcess();
		fork.printChildDataToConsole();
		int exitValue = fork.cloneOwnProcess(TestMainClass2.class);
		assertEquals(0, exitValue);
		assertEquals(0, fork.getOutputData().size());
		assertEquals(0, fork.getErrorData().size());
		assertFalse(fork.IsProcessesRunning());
	}

	@Test
	public void testChildProcessReturnError() {
		// preparation
		// test
		ChildProcess fork = new ChildProcess();
		int exitValue = fork.cloneOwnProcess(TestMainClass3.class);
		assertEquals(TestMainClass3.EXITCODE, exitValue);
		assertOutputData(fork, TestMainClass3.HI);
		assertEquals(1, fork.getErrorData().size());
		assertEquals(TestMainClass3.ERRORMSG, fork.getErrorData().get(0));
		assertFalse(fork.IsProcessesRunning());
	}

	@Test(timeout = 5000)
	// ms
	public void testDestroyChildProcess() {
		// preparation
		ChildProcess fork = new ChildProcess();
		(new Thread(new ProcessDestroyer(fork))).start();
		// test
		int exitValue = fork.cloneOwnProcess(TestMainClass5.class);
		assertFalse(fork.IsProcessesRunning());
		if (Utils.isWindows()) {
			assertEquals(TestMainClass5.EXITCODE_PROCESS_DESTROYED_ON_WINDOWS,
					exitValue);
		} else if (Utils.isLinux()) {
			assertEquals(TestMainClass5.EXITCODE_PROCESS_DESTROYED_ON_LINUX,
					exitValue);
		} else {
			fail();
		}
	}

	@Test
	public void testErrorForkChildMainClassHasNoMain() {
		// preparation
		ChildProcess fork = new ChildProcess();
		// test
		int exitValue = fork.cloneOwnProcess(TestMainClass4.class);
		printOutputData(fork);
		assertEquals(1, exitValue); // identified this by experimentation on
									// windows
	}

	@Test
	public void testFutureProcessReturnsOneLine() throws InterruptedException,
			ExecutionException {
		// preparation
		// test
		ChildProcess fork = new ChildProcess();
		FutureTask<Integer> futureTask = fork
				.createTaskCloneOwnProcess(TestMainClass1.class);
		ExecutorService executor = Executors.newFixedThreadPool(1);
		executor.execute(futureTask);
		int exitValue = futureTask.get().intValue();
		assertEquals(0, exitValue);
		assertOutputData(fork, TestMainClass1.HI);
		assertEquals(0, fork.getErrorData().size());
	}

	static class TestMainClass1 {
		public final static String[] HI = new String[] { "hi1" };

		public static void main(String[] args) {
			printStrings(HI);
			System.out.flush();
			System.exit(0);
		}
	}

	static class TestMainClass2 {
		public final static String[] HI = new String[] { "hi1", "hi2", "hi3" };

		public static void main(String[] args) {
			printStrings(HI);
			System.out.flush();
			System.exit(0);
		}
	}

	static class TestMainClass3 {
		public final static int EXITCODE = 10;
		public final static String ERRORMSG = "Help";
		public final static String[] HI = new String[] { "hi1" };

		public static void main(String[] args) {
			printStrings(HI);
			System.out.flush();
			System.err.println(ERRORMSG);
			System.err.flush();
			System.exit(EXITCODE);
		}
	}

	static class TestMainClass4 {
		// has no main(String[] args)
	}

	static class TestMainClass5 {
		public final static int EXITCODE_NOK = 15;
		public final static int EXITCODE_OK = 0;
		public final static int EXITCODE_PROCESS_DESTROYED_ON_WINDOWS = 1; // returned
																			// when
																			// process
																			// killed
																			// from
																			// Java
		public final static int EXITCODE_PROCESS_DESTROYED_ON_LINUX = 143; // returned
																			// when
																			// process
																			// killed
																			// from
																			// Java

		public static void main(String[] args) {
			try {
				TimeUnit.SECONDS.sleep(30);
				System.exit(EXITCODE_NOK);
			} catch (InterruptedException e) {
				System.exit(EXITCODE_OK);
			}
			System.exit(EXITCODE_OK);
		}
	}

	static class ProcessDestroyer implements Runnable {
		private final ChildProcess fork;

		ProcessDestroyer(final ChildProcess fork) {
			this.fork = fork;
		}

		@Override
		public void run() {
			try {
				TimeUnit.SECONDS.sleep(2);
			} catch (InterruptedException e) {
				fail();
			}
			fork.destroyChild();
		}

	}
}
