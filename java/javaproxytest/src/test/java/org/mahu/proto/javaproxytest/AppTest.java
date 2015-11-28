package org.mahu.proto.javaproxytest;

import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class AppTest {
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Test
	public void testJavaProxy() {
		Factory<String> bc = FuncJavaProxy.nop(String.class);
		String obj = bc.get();
		assertTrue(obj != null);
	}

	/**
	 * You can not create an instance by using the class from the proxy.
	 */
	@Test
	public void testJavaProxyOwnInstance() throws InstantiationException,
			IllegalAccessException {
		Factory<String> bc = FuncJavaProxy.nop(String.class);
		Class<?> cls = bc.getClass();
		exception.expect(java.lang.InstantiationException.class);
		cls.newInstance();
	}

	@Test
	public void testSameClassesAreGenerated() {
		Factory<String> bc1 = FuncJavaProxy.nop(String.class);
		Factory<Integer> bc2 = FuncJavaProxy.nop(Integer.class);
		assertTrue(bc1.getClass().getName().equals(bc2.getClass().getName()));
	}

}
