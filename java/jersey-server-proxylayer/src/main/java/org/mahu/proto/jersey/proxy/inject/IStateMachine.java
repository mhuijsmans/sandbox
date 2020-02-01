package org.mahu.proto.jersey.proxy.inject;

import java.util.function.Supplier;

public interface IStateMachine {

	Object executeRequest(final Class<?> interfaceClass, Supplier<Object> responseSupplier);

}
