package org.mahu.proto.junit;

import org.junit.Rule;
import org.junit.Test;

public class MyTestResourceRuleTest {

	@Rule
	public org.junit.rules.ExternalResource rule = new MyTestResourceRule();

	@Test
	public void test1() {
		System.out.println("test1");
	}
	
	@Test
	public void test2() {
		System.out.println("test2");
	}	

}
