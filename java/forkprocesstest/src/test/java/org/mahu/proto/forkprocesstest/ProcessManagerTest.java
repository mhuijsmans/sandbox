package org.mahu.proto.forkprocesstest;

import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ProcessManagerTest extends ProcessTestBase {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Test(timeout=5000)
	public void testSpawnProcess() throws InterruptedException {
		// preparation
		ProcessManager pm = new ProcessManager(1);
		assertEquals(0, pm.GetNrOfRunningChildprocesses());
		pm.StartProcess(TestMainClass.class);
		Thread.sleep(1000);
		assertEquals(1, pm.GetNrOfRunningChildprocesses());
		Thread.sleep(1500);		
		assertEquals(0, pm.GetNrOfRunningChildprocesses());
		Thread.sleep(1500);			
	}
	
	static class TestMainClass {

		public static void main(String[] args) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
			System.exit(0);
		}
	}

}
