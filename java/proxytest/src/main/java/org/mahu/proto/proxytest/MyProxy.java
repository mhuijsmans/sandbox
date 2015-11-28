package org.mahu.proto.proxytest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.logging.Logger;

public class MyProxy implements InvocationHandler {
	private static final Logger log = Logger.getLogger(MyProxy.class.getName());

	private Object testImpl;

	public MyProxy(Object impl) {
		this.testImpl = impl;
	}

	// @Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		// Note: using proxy in next log statement, causes stackOverflow, because
		// it results in another invoke(..)
		log.fine("invoke proxy: " + " method: " + method + " args");
		if (Object.class == method.getDeclaringClass()) {
			String name = method.getName();
			if ("equals".equals(name)) {
				return proxy == args[0];
			} else if ("hashCode".equals(name)) {
				return System.identityHashCode(proxy);
			} else if ("toString".equals(name)) {
				return proxy.getClass().getName() + "@"
						+ Integer.toHexString(System.identityHashCode(proxy))
						+ ", with InvocationHandler " + this;
			} else {
				throw new IllegalStateException(String.valueOf(method));
			}
		}
		return method.invoke(testImpl, args);
	}
}
