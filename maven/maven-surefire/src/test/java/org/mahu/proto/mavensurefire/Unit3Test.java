package org.mahu.proto.mavensurefire;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class Unit3Test {
	@Test
	public void testApp() {
		System.out.println(Unit3Test.class.getSimpleName()+", pid="+OsSystem.getProcessId("<pid>"));	
		assertTrue(true);
	}
}
