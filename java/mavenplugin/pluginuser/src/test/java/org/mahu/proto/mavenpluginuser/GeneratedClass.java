package org.mahu.proto.mavenpluginuser;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Test;

/**
 * This class verifies that the code is generated. Because the generated class
 * does not exist when this test is written, reflection is used for testing.
 */
public class GeneratedClass {

	@Test
	public void classIsGenerated() throws ClassNotFoundException {
		Class.forName("org.mahu.proto.generated.test.GeneratedClass");
	}

	@Test
	public void ping_correctResponse() throws ClassNotFoundException, InstantiationException, IllegalAccessException,
			NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
		Class<?> clazz = Class.forName("org.mahu.proto.generated.test.GeneratedClass");
		Object obj = clazz.newInstance();

		final String methodName = "ping";
		final Class<?>[] methodArgumentTypes = new Class<?>[0];
		Method method = obj.getClass().getMethod(methodName, methodArgumentTypes);
		final Object[] methodArguments = new Object[0];
		Object response= method.invoke(obj, methodArguments);
		assertNotNull(response);
		assertTrue(response instanceof String);
		assertEquals("pong", response);
	}

}
