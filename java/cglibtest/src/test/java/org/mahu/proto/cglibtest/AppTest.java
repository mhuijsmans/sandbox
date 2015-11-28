package org.mahu.proto.cglibtest;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class AppTest {
	
	@Test
	public void testCgLibProxyString() {
		BaseClass<String> bc = FuncCgLib.nop(String.class);
		String obj = bc.get();
		assertTrue(obj instanceof String);
	}
	@Test
	public void testCgLibProxyInteger() {
		BaseClass<StringBuffer> bc = FuncCgLib.nop(StringBuffer.class);
		StringBuffer obj = bc.get();
		assertTrue(obj instanceof StringBuffer);
	}
	
    @Test
    public void testSameClassesAreGeneratedFromSamebaseClass() {
           Factory<StringBuffer> bc2 = FuncCgLib.nop(StringBuffer.class);
           Factory<String> bc1 = FuncCgLib.nop(String.class);           
           String className1 = bc1.getClass().getName();
           String className2 = bc2.getClass().getName();
           System.out.println("className1: "+className1);
           System.out.println("className2: "+className2);
           // It seems that because class is generated from same class (i.e. baseClass), that 
           assertTrue(className1.equals(className2));
    }   	

}
