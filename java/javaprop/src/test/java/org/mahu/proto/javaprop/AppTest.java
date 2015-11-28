package org.mahu.proto.javaprop;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class AppTest {
	@Test
	public void testPrintProperties() {
		App.printProperties();
		assertTrue(true);
	}
	
	@Test
	public void testPrintEnv() {
		App.printEnvironment();
		assertTrue(true);
	}
	
	@Test
	public void testCpuInfo() {
		System.out.println("AvailableProcessors (threads): "+Runtime.getRuntime().availableProcessors());
		assertTrue(true);
	}	
}
