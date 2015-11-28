package org.mahu.proto.forkprocesstest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mahu.proto.forkprocesstest.ChildProcess.ForkProcessException;

public class StartChildProcessTest extends ProcessTestBase {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Test
	public void testSpawnProcess() {
		// preparation
		List<String> command = createCommandListContentCurrentDirectory();
		ChildProcess fork = new ChildProcess();
		// test
		int exitValue = fork.forkProcess(command);
		assertEquals(0, exitValue);
		printOutputData(fork);
	}

	protected List<String> createCommandListContentCurrentDirectory() {
		List<String> command = null;
		if (Utils.isWindows()) {
			command = Arrays.asList("cmd.exe", "/c", "dir");
		} else if (Utils.isLinux()) {
			command = Arrays.asList("ls");
		} else {
			fail();
		}
		return command;
	}

	@Test
	public void testErrorSpawnProcess() {
		// preparation
		List<String> command = createInvalidCommand();		
		ChildProcess fork = new ChildProcess();
		// test
		exception.expect(ForkProcessException.class);
		fork.forkProcess(command);
	}
	
	protected List<String> createInvalidCommand() {
		List<String> command = null;
		if (Utils.isWindows()) {
			command = Arrays.asList("cmdcmd.exe", "/c", "dir");
		} else if (Utils.isLinux()) {
			command = Arrays.asList("lslslslsls");
		} else {
			fail();
		}
		return command;
	}	

}
