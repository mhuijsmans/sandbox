package org.mahu.proto.xsdtest;

import static org.junit.Assert.*;

import org.junit.Test;
import org.mahu.proto.xsdtest.test.schema.ObjectFactory;
import org.mahu.proto.xsdtest.test.schema.PowerState3Type;

public class PowerState3Test {

	@Test
	public void test() {
		// This test case verifies that for the test XSD code is generated.
		assertNotNull(new ObjectFactory().createPowerState3(PowerState3Type.ON));
	}

}
