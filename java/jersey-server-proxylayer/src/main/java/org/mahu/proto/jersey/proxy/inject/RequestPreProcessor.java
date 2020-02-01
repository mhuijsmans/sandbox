package org.mahu.proto.jersey.proxy.inject;

import java.lang.reflect.Proxy;
import java.util.function.Supplier;

import javax.inject.Inject;

import com.google.inject.Injector;

public final class RequestPreProcessor implements IRequestPreProcessor {

	private final Injector injector;
	private final StateMachine stateMachine = new StateMachine();

	@Inject
	public RequestPreProcessor(final Injector injector) {
		this.injector = injector;
	}

	@Override
	public <T> T with(final Class<T> requestInterfaceClass) {
		final RequestInterface<T> requestInterface = new RequestInterface<>(requestInterfaceClass);
		final InstanceProvider<T> instanceProvider = new InstanceProvider<>(injector, requestInterface);
		final RequestInvocationHandler<T> handler = new RequestInvocationHandler<T>(instanceProvider, stateMachine);
		@SuppressWarnings("unchecked")
		final T f = (T) Proxy.newProxyInstance(requestInterfaceClass.getClassLoader(), new Class<?>[] { requestInterfaceClass },
				handler);
		return f;
	}
}