package org.mahu.proto.reflectiontest;

import java.lang.reflect.Field;

public class TestClass {

	static {
		try {
			FIELD1 = TestClass.class.getDeclaredField("str1");
			FIELD2 = TestClass.class.getDeclaredField("str2");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Field FIELD1;
	public static Field FIELD2;

	private String str1;
	private TestClass str2;

	public String getStr1() {
		return str1;
	}

	public void setStr1(final String s) {
		str1 = s;
	}

	public TestClass getStr2() {
		return str2;
	}

	public int length() {
		return 0;
	}

}
