package org.mahu.proto.javaproxytest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.logging.Logger;

public class FuncJavaProxy {

	private final static Logger LOGGER = Logger.getLogger(FuncJavaProxy.class
			.getName());

	public static <T> Factory<T> nop(final Class<T> clz) {
		InvocationHandler handler = new TestInvocationHandler(clz);
		@SuppressWarnings("unchecked")
		Factory<T> t = (Factory<T>) Proxy.newProxyInstance(
				Factory.class.getClassLoader(),
				new Class<?>[] { Factory.class },
				handler);		
		return t;
	}

	static class TestInvocationHandler implements InvocationHandler {
		private Object proxy;
		private final Class<?> clazz = BaseClass.class;

		public TestInvocationHandler(final Class<?> clz) {
			try {
				this.proxy = clazz.getConstructor(Class.class).newInstance(clz);
				LOGGER.info("Created proxy of instance " + clazz.getName());
			} catch (InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			LOGGER.info("intercept method : "+method);				
			if (Object.class == method.getDeclaringClass()) {
				String name = method.getName();
				if ("equals".equals(name)) {
					return proxy == args[0];
				} else if ("hashCode".equals(name)) {
					return System.identityHashCode(proxy);
				} else if ("toString".equals(name)) {
					return proxy.getClass().getName()
							+ "@"
							+ Integer.toHexString(System
									.identityHashCode(proxy))
							+ ", with InvocationHandler " + this;
				} else {
					throw new IllegalStateException(String.valueOf(method));
				}
			}
			return method.invoke(this.proxy, args);
		}
	}

}
