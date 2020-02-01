package org.mahu.proto.jersey.proxy.inject;

import com.google.inject.Module;

/**
 * This class encapsulates the request interface class.
 */
final class RequestInterface<T> {
	
	private final  Class<T> requestInterfaceClass;
	
	public RequestInterface(final Class<T> requestInterfaceClass) {
		this.requestInterfaceClass = requestInterfaceClass;
	}
	
	Class<? extends Module> getModuleClass() {
		return requestInterfaceClass.getAnnotation(RequestProperties.class).moduleClass();
	}
	
	Class<? extends IRequestScopeMapProvider> getRequestScopedDataProvider() {
		return requestInterfaceClass.getAnnotation(RequestProperties.class).requestScopedMapProvider();
	}
	
	boolean isModuleDefined() {
		return getModuleClass() != EmptyModule.class;
	}

	public Class<T> getInterfaceClass() {
		return requestInterfaceClass;
	}

	public String getInterfaceClassName() {
		return requestInterfaceClass.getName();
	}

}
