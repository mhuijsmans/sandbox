package org.mahu.proto.jersey.proxy.statemachine;

public interface IStateController {

	State getState();

	<T> T execute(ICallable<T> callable);

}
