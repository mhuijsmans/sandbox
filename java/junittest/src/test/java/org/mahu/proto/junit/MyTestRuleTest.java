package org.mahu.proto.junit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mahu.proto.junit.annotation.MyClassAnnotation;
import org.mahu.proto.junit.annotation.MyMethodAnnotation;

@MyClassAnnotation
public class MyTestRuleTest {

	@Rule
	public TestRule rule = new MyTestRule();

	@Test
	public void test1() {
	}
	
	@Test
	@MyMethodAnnotation (param="hi")
	public void test2() {
	}	

}
