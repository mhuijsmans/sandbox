package org.mahu.proto.forkprocesstest;

import org.junit.Test;

/**
 * If you want to fork you need to have knowledge of the properties & environment 
 */
public class PrintSystemEnvironmentAndPropertiesTest {
	@Test
	public void testAllProperties() {
		Utils.printAllProperties();
	}
	
	@Test
	public void testSomeProperties() {
		Utils.printSomeProperties();
	}
	
	@Test
	public void testAllEnvironment() {
		Utils.printAllEnvironmentData();
	}	
}
