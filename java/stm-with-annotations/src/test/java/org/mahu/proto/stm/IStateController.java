package org.mahu.proto.stm;

public interface IStateController {

	State getState();

	<T> T execute(ICallable<T> callable);

}
