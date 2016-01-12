package org.mahu.proto.lifecycle;

import java.util.Optional;

public interface IApiBroker {

	/**
	 * resolve an implementation of provided type
	 * @return
	 */
	<T> Optional<T> resolve(Class<T> requestedInterface);

}
