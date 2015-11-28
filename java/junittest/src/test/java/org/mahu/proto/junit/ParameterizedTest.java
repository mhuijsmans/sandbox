package org.mahu.proto.junit;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ParameterizedTest {

	// creates the test data
	// This data is pass through the constructor before the test is executed.
	// The number of entries in the list determines number of tests.
	// The method must be static.
	@Parameters
	public static Collection<Object[]> data() {
		Object[][] data = new Object[][] { { 1 }, { 5 }, { 121 } };
		return Arrays.asList(data);
	}

	private int multiplier;

	public ParameterizedTest(int aMultiplier) {
		this.multiplier = aMultiplier;
	}

	@Test
	public void testMultiplyException() {
		TestClass tester = new TestClass();
		assertEquals("Result", multiplier * multiplier,
				tester.multiply(multiplier, multiplier));
	}
	
	static class TestClass {
		
		public int multiply(int a,int b) {
			return a*b;
		}
		
	}

}
