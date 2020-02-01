package org.mahu.proto;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.junit.Test;

public class AppTest {

	static class HandlerCallProcessor implements InvocationHandler {

		HandlerCallProcessor() {
		}

		public Object invoke(Object proxy, Method method, Object[] arguments)
				throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			return null;
		}
	}

	static class MemoryPrint {
		private final Runtime rt = Runtime.getRuntime();
		long prevTotal = 0;
		long prevFree = rt.freeMemory();
		int iteration = 0;

		void print() {
			long total = rt.totalMemory();
			long free = rt.freeMemory();

			long used = total - free;
			long prevUsed = (prevTotal - prevFree);
			System.out.println("#" + iteration + ", Total: " + total + ", Used: " + used + ", ∆Used: " + (used - prevUsed)
					+ ", Free: " + free + ", ∆Free: " + (free - prevFree));
			prevTotal = total;
			prevFree = free;
			iteration++;
		}
	}

	@Test
	public void testCreateProxy() {
		HandlerCallProcessor handler = new HandlerCallProcessor();
		final int max = 1000 * 1000 * 1000;
		MemoryPrint memoryPrint = new MemoryPrint();
		memoryPrint.print();
		for (int j = 0; j < 100; j++) {
			for (int i = 0; i < max; i++) {
				Proxy.newProxyInstance(TestInterface.class.getClassLoader(), new Class[] { TestInterface.class },
						handler);
			}
			memoryPrint.print();			
		}
	}

}
