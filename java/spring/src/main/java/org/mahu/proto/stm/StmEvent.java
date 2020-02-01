package org.mahu.proto.stm;

import java.util.function.Supplier;

public final class StmEvent implements IStmEvent {

	public StmEvent(State[] requiredState) {
	}

	@Override
	public IStmEvent thenTransitionTo(State newState) {
		return this;
	}
	
	@Override
	public IStmEvent registerEvent(Event x) {
		return this;
	}
	
	@Override
	public <T> T andCall(Supplier<T> supplier) {
		return supplier.get();
	}

}
