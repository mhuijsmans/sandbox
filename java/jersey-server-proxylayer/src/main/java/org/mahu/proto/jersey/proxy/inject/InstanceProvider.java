package org.mahu.proto.jersey.proxy.inject;

import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * The InstanceProvider is a provider for the creation of Object using Guice 
 */
final class InstanceProvider<T> {
	private final Injector injector;
	private final RequestInterface<T> requestInterface;

	InstanceProvider(final Injector injector, final RequestInterface<T> requestInterface) {
		this.injector = injector;
		this.requestInterface = requestInterface;
	}

	RequestInterface<T> getRequestInterface() {
		return requestInterface;
	}

	Object getInstance() {
		if (requestInterface.isModuleDefined()) {
			try {
				// TODO: This creates a module for every request. That is not what wanted. Instead use a Resolver to find a predefined map.
				// TODO: Child module or alternative module. With predefined, go for alternative (given that there will be a predefined configuration).
				// Predefined means easier overview and ability to create a Graph.
				Module childModule = createChildModule();
				Injector childInjector = createChildInjector(childModule);
				return createInstance(childInjector);
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		} else {
			return createInstance(injector);
		}
	}

	private Module createChildModule() throws InstantiationException, IllegalAccessException {
		return requestInterface.getModuleClass().newInstance();
	}

	private Object createInstance(Injector childInjector) {
		try {
			return childInjector.getInstance(requestInterface.getInterfaceClass());
		} catch (RuntimeException e) {
			throw createException("creating instance", e);
		}
	}

	private Injector createChildInjector(Module childModule) {
		try {
			return injector.createChildInjector(childModule);
		} catch (RuntimeException e) {
			throw createException("creating ChildInjector", e);
		}
	}

	private RuntimeException createException(String subMsg, RuntimeException e) {
		String message = "Error " + subMsg + " for interface=" + requestInterface.getInterfaceClassName();
		return new RuntimeException(message, e);
	}
}