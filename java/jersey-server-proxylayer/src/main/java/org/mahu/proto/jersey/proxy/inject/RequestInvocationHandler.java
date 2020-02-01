package org.mahu.proto.jersey.proxy.inject;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Supplier;

import com.google.inject.Key;

final class RequestInvocationHandler<T> implements InvocationHandler {

	private final InstanceProvider<T> instanceProvider;
	private final StateMachine stateMachine;

	RequestInvocationHandler(final InstanceProvider<T> instanceProvider, final StateMachine stateMachine) {
		this.instanceProvider = instanceProvider;
		this.stateMachine = stateMachine;
	}

	@Override
	public Object invoke(final Object proxy, final Method method, final Object[] arguments)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		final Supplier<Object> responseSupplier = createResponseSupplier(method, arguments);
		return stateMachine.executeRequest(getRequestInterface().getInterfaceClass(), responseSupplier);
	}

	Supplier<Object> createResponseSupplier(final Method method, final Object... arguments) {
		final Supplier<Object> responseSupplier = () -> {
			final Map<Key<?>, Object> requestScopedObjects = getMapWithRequestScopedObjects();
			final ExecutorUsingRequestScope executor = new ExecutorUsingRequestScope(requestScopedObjects);
			return executor.execute(() -> {
				try {
					final Object obj = instanceProvider.getInstance();
					return method.invoke(obj, arguments);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new RuntimeException(e);
				}
			});
		};
		return responseSupplier;
	}

	private Map<Key<?>, Object> getMapWithRequestScopedObjects()  {
		try {
			IRequestScopeMapProvider requestScopeMapProvider =  getRequestInterface().getRequestScopedDataProvider().newInstance();
			return requestScopeMapProvider.getMap();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private RequestInterface<T> getRequestInterface() {
		return instanceProvider.getRequestInterface();
	}
}