package org.mahu.proto.mavensurefire;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class Unit2Test {
	@Test
	public void testApp() {
		System.out.println(Unit2Test.class.getSimpleName()+", pid="+OsSystem.getProcessId("<pid>"));		
		assertTrue(true);
	}
}
