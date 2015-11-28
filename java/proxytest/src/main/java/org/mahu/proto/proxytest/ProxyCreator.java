package org.mahu.proto.proxytest;

import java.lang.reflect.Proxy;

import org.mahu.proto.proxytest.remote.RequestExecutor;
import org.mahu.proto.proxytest.remote.RequestProxy;

public class ProxyCreator {

	private static RequestExecutor requestExecutor;

	public static void setRequestExecutor(final RequestExecutor requestExecutor) {
		ProxyCreator.requestExecutor = requestExecutor;
	}

	public static TestInterface1 createTestInterfaceProxy(
			TestInterface1 myTestInterface) {
		return (TestInterface1) Proxy.newProxyInstance(TestInterface1.class
				.getClassLoader(), new Class<?>[] { TestInterface1.class },
				new MyProxy(myTestInterface));
	}

	public static TestInterface2 createTestInterface2Proxy(
			TestInterface2 myTestInterface) {
		return (TestInterface2) Proxy.newProxyInstance(TestInterface2.class
				.getClassLoader(), new Class<?>[] { TestInterface2.class },
				new MyProxy(myTestInterface));
	}

	public static TestInterface1 createTestInterface1RemoteProxy() {
		return createTestInterface1RemoteProxy(TestInterface1.class);
	}

	public static <T> T createTestInterface1RemoteProxy(final Class<T> intf) {
		return (T) Proxy.newProxyInstance(intf.getClassLoader(),
				new Class<?>[] { intf },
				new RequestProxy(intf, requestExecutor));
	}

}
