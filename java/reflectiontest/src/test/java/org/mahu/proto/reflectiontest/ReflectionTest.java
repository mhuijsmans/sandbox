package org.mahu.proto.reflectiontest;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ReflectionTest {

	class MyTestClass extends TestClass {
	}

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	public void before() throws SecurityException, NoSuchFieldException {
		TestClass.FIELD1.setAccessible(false);
		TestClass.FIELD1.setAccessible(false);
	}

	// Test that JVM does not allow me to set a String field to an Integer
	@Test
	public void errorSetStringFieldToInteger() throws NoSuchFieldException,
			SecurityException, IllegalArgumentException, IllegalAccessException {
		// preparation
		Field field = TestClass.FIELD1;
		field.setAccessible(true);
		TestClass tc = new TestClass();
		// test
		exception.expect(IllegalArgumentException.class);
		field.set(tc, new Integer(1));
	}

	// Test that I can set private field (if accessible is set to true)
	@Test
	public void setStringFieldToString() throws NoSuchFieldException,
			SecurityException, IllegalArgumentException, IllegalAccessException {
		// preparation
		Field field = TestClass.FIELD1;
		field.setAccessible(true);
		TestClass tc = new TestClass();
		// test
		field.set(tc, new String());
		assertTrue(tc.getStr1().length() == 0);
	}

	// Test that I can set field to derived class
	@Test
	public void setFieldToDerivedClass() throws NoSuchFieldException,
			SecurityException, IllegalArgumentException, IllegalAccessException {
		// preparation
		Field field = TestClass.FIELD2;
		field.setAccessible(true);
		TestClass tc = new TestClass();
		// test
		field.set(tc, new MyTestClass());
		assertTrue(tc.getStr2().length() == 0);
	}

	// Test that I can NOT set String to object of other class. I will get an exception
	@Test
	public void setStringToObjectOfOtherClass() throws NoSuchFieldException,
			SecurityException, IllegalArgumentException,
			IllegalAccessException, ClassNotFoundException,
			NoSuchMethodException, InvocationTargetException {
		// preparation
		TestClass tc = new TestClass();
		String className = TestClass.class.getName();
		Class<?> cls = Class.forName(className);
		Class<?>[] methodArgClasses = new Class<?>[] { String.class };
		Method method = cls.getMethod("setStr1", methodArgClasses);
		Object[] invokeArgs = new Object[] { tc };
		// test
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("argument type mismatch");
		method.invoke(tc, invokeArgs);
	}

	// Test that I can not set private field
	@Test
	public void errorCanNotSetPrivateField() throws NoSuchFieldException,
			SecurityException, IllegalArgumentException, IllegalAccessException {
		// preparation
		Field field = TestClass.FIELD1;
		TestClass tc = new TestClass();
		// test
		exception.expect(IllegalAccessException.class);
		field.set(tc, new String());
	}

	// Test that I can not set private field, after I set accessible to true and
	// next false
	@Test
	public void errorCanNotSetPrivateFieldIfAccessibleIsSetToFalse()
			throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {
		// preparation
		Field field = TestClass.FIELD1;
		field.setAccessible(true);
		field.setAccessible(false);
		TestClass tc = new TestClass();
		// test
		exception.expect(IllegalAccessException.class);
		field.set(tc, new String());
	}
}
