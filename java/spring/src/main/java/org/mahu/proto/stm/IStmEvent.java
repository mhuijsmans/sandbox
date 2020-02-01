package org.mahu.proto.stm;

import java.util.function.Supplier;

public interface IStmEvent {

	IStmEvent thenTransitionTo(State newState);
	
	IStmEvent registerEvent(Event x);

	<T> T andCall(Supplier<T> supplier);

}
