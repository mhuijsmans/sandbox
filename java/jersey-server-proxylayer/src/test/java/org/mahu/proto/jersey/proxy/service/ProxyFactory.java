package org.mahu.proto.jersey.proxy.service;

import java.lang.reflect.Proxy;

public final class ProxyFactory {
	
	public static IRestService createProxy() {
		IRestService mapProxyInstance = (IRestService) Proxy.newProxyInstance(
				ProxyFactory.class.getClassLoader(), new Class[] { IRestService.class }, 
				  new ForwardingInvocationHandler(new RestService()));
		return mapProxyInstance;
	}

}
