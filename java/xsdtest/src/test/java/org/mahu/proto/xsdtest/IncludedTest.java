package org.mahu.proto.xsdtest;

import static org.junit.Assert.*;

import org.junit.Test;
import org.mahu.proto.xsdtest.schema.IncludedType;
import org.mahu.proto.xsdtest.schema.ObjectFactory;

public class IncludedTest {

	@Test
	public void test() {
		// This test case verifies that for the XSD that includes another XSD, code is generated.
		assertNotNull(new ObjectFactory().createIncluded(IncludedType.ON));
	}

}
