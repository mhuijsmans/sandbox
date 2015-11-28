package org.mahu.proto.bigobjects;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;
import org.mahu.proto.forkprocesstest.ChildProcess;

public class MainTest extends ProcessTestBase {

	@Ignore
	@Test
	public void testSpawnProcessDefaultJvmOptions() {
		ChildProcess fork = new ChildProcess();
		int exitValue = fork.cloneOwnProcess(org.junit.runner.JUnitCore.class,
				AllocateManyBigObjectsJavaTest.class.getName());
		printOutputData(fork);
		assertEquals(0, exitValue);
	}

	@Ignore
	@Test
	public void testSpawnProcessJvmOptionsExtraMemory() {
		ChildProcess fork = new ChildProcess();
		// http://docs.oracle.com/javase/8/docs/technotes/tools/windows/java.html#BABDJJFI
		// -Xms sets the initial size (in bytes) of the heap
		// -Xmx specifies the maximum size (in bytes) of the memory allocation
		// pool in bytes.
		String[] jvmOptions = new String[] { "-Xms4g", "-Xmx4g" };
		fork.setJvmOptions(jvmOptions);
		int exitValue = fork.cloneOwnProcess(org.junit.runner.JUnitCore.class,
				AllocateManyBigObjectsJavaTest.class.getName());
		printOutputData(fork);
		assertEquals(0, exitValue);
	}
	
	@Test
	public void testSpawnProcessNative() {
		printLibraryPath();		
		ChildProcess fork = new ChildProcess();
		String[] jvmOptions = new String[] { "-Djava.library.path="+getCurrentLibraryPath() };
		fork.setJvmOptions(jvmOptions);		
		int exitValue = fork.cloneOwnProcess(org.junit.runner.JUnitCore.class,
				AllocateManyBigObjectsCppTest.class.getName());
		fork.printChildDataToConsole();
		printOutputAndErrorData(fork);
		assertEquals(0, exitValue);
	}	

}
