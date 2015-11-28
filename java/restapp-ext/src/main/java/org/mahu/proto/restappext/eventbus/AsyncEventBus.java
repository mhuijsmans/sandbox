package org.mahu.proto.restappext.eventbus;

/**
 * AsyncEventBus processes all event asynchronous. Events should be immutable.
 */
public interface AsyncEventBus {

	/**
	 * Post event for processing by subscribers.
	 * Event should be immutable.
	 * @param obj
	 */
	public void Post(Object obj);

}
