package org.mahu.proto.forkprocesstest;

import static org.junit.Assert.assertEquals;

import java.lang.management.ManagementFactory;
import java.util.List;

import org.junit.Test;

public class JunitChildForkTest extends ProcessTestBase {
	// at
	// org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.main(RemoteTestRunner.java:192)

	@Test
	public void testSpawnProcessOnWindows() {
		System.out.println(ManagementFactory.getRuntimeMXBean().getName());
		ChildProcess fork = new ChildProcess();
		int exitValue = fork.cloneOwnProcess(org.junit.runner.JUnitCore.class,
				JunitChildTest.class.getName());
		printOutput(fork.getOutputData());
		printOutput(fork.getErrorData());
		assertEquals(0, exitValue);
	}

	private void printOutput(List<String> data) {
		int l = data.size();
		int i = 0;
		while (i < l) {
			System.out.println(data.get(i++));
		}
	}
}
