package org.mahu.proto.mavensurefire;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class Unit1Test {
	@Test
	public void testApp() {
		System.out.println(System.getenv("LIBRARY_TYPEOFTEST"));
		System.out.println(System.getProperty("LIBRARY_TYPEOFTEST"));
		System.out.println(Unit1Test.class.getSimpleName()+", pid="+OsSystem.getProcessId("<pid>"));
		
		assertTrue(true);
	}
}
