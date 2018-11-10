package org.mahu.proto.xsdtest;

import static org.junit.Assert.fail;

import org.junit.Test;

public class PowerState4Test {

	@Test
	public void test() throws ClassNotFoundException {
		// This test case verifies that for PowerState4 XSD exists but that no code is generated.
		try {
			Class.forName("org.mahu.proto.xsdtest.schema.PowerState4");
			fail("No code shall be generated for this PowerState4.xsd");
		} catch (ClassNotFoundException e) {
			// excepted
		}
		
		// But for this code is generated
		Class.forName("org.mahu.proto.xsdtest.schema.PowerState");
	}

}
