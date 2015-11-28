package org.mahu.proto.spring.test;

import javax.inject.Named;

import org.mahu.proto.spring.MessageService1;

@Named("instance1")
public class TestComponent1 implements MessageService1 {
	public static String NAME = TestComponent1.class.getName();
	private String name;

	TestComponent1() {
		name = NAME;
	}

	public String getName() {
		return name;
	}
}
