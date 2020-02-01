package org.mahu.proto.jersey.proxy.statemachine;

import java.util.Optional;

public final class StateController implements IStateController {

	private final StateInfo stateInfo;

	StateController(State initialState) {
		stateInfo = new StateInfo(initialState);
	}

	public <T> T execute(ICallable<T> callable) {
		final Stm stm = callable.getClass().getAnnotation(Stm.class);
		final State[] allowedInStates = stm.allowedInStates();
		CallableExecutor callableExecutor;
		synchronized (stateInfo) {
			if (isRequestAllowed(allowedInStates)) {
				Optional<State>newState = stm.transitionToState().getNewState();
				if (newState.isPresent()) {
					callableExecutor = new CallableExecutorStateUpdated(stateInfo);
					stateInfo.setNewState(newState.get());					
				} else {
					callableExecutor = new CallableExecutorNoTransition(stateInfo);
				}
			} else {
				callableExecutor = new CallableExecutorRequestNotAllowed(stateInfo);
			}
		}
		return callableExecutor.execute(callable);
	}

	private boolean isRequestAllowed(State[] allowedInStates) {
		final State currentState = stateInfo.getCurrentState();
		for (State state : allowedInStates) {
			if (state == currentState) {
				return true;
			}
		}
		return false;
	}

	public State getState() {
		synchronized(stateInfo) {
			return stateInfo.getCurrentState();
		}
	}

}
