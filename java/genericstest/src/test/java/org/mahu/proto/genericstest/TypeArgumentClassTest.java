package org.mahu.proto.genericstest;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TypeArgumentClassTest {

	/**
	 * Test with direct inheritance, single parameterType
	 */	
	static class AA extends ClassA<String> {
	}

	@Test
	public void testClassGenericsTypeT() {
		AA aa = new AA();
		assertTrue(aa.getClassOfT().equals(String.class));
	}

	/**
	 * Test with direct inheritance, multiple parameterTypes
	 */
	static class BB extends ClassB<String, Integer> {
	}

	@Test
	public void testClassGenericsTypeSandT() {
		BB bb = new BB();
		assertTrue(bb.getClassOfS().equals(String.class));
		assertTrue(bb.getClassOfT().equals(Integer.class));
	}

	/**
	 * Test with indirect inheritance
	 */
	
	static class BBB extends BB {
	}

	@Test
	public void testClassGenericsTypeSandTIndirect() {
		BBB bb = new BBB();
		assertTrue(bb.getClassOfS().equals(String.class));
		assertTrue(bb.getClassOfT().equals(Integer.class));
	}	
}
