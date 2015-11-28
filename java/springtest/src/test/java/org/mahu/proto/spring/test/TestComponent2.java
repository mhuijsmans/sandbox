package org.mahu.proto.spring.test;

import javax.inject.Named;

import org.mahu.proto.spring.MessageService1;

@Named("instance2")
public class TestComponent2 implements MessageService1 {
	public static String NAME = TestComponent2.class.getName();
	private String name;

	TestComponent2() {
		name = NAME;
	}

	public String getName() {
		return name;
	}
}
