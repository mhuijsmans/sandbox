package org.mahu.proto.jersey.proxy.inject;

import java.util.function.Supplier;

final class StateMachine implements IStateMachine {

	@Override
	public Object executeRequest(Class<?> interfaceClass, Supplier<Object> responseSupplier) {
		Object result = responseSupplier.get();
		return result;
	}

}
