package org.mahu.proto.stm;

import java.util.Optional;

abstract class CallableExecutor {

	private final StateInfo stateInfo;

	CallableExecutor(final StateInfo stateInfo) {
		this.stateInfo = stateInfo;
	}

	protected StateInfo getStateInfo() {
		return stateInfo;
	}

	protected State getCurrentState() {
		return stateInfo.getCurrentState();
	}

	protected void setNewState(State newState) {
		synchronized (getStateInfo()) {
			stateInfo.setNewState(newState);
		}
	}

	/**
	 * Default implementation, it execute callable and updates state to FATAL in case of an fatal exception.  
	 * 
	 * @param callable
	 * @return the result of the call.execute()
	 */
	<T> T execute(ICallable<T> callable) {
		try {
			return callable.execute();
		} catch (RuntimeException e) {
			if (e instanceof FatalException) {
				setNewState(State.FATAL);
			}
			throw e;
		}
	}
}

final class CallableExecutorRequestNotAllowed extends CallableExecutor {

	CallableExecutorRequestNotAllowed(final StateInfo stateInfo) {
		super(stateInfo);
	}

	public <T> T execute(ICallable<T> callable) {
		State[] allowedInStates = callable.getClass().getAnnotation(Stm.class).allowedInStates();
		throw createRequestNotAllowedException(allowedInStates);
	}

	private RuntimeException createRequestNotAllowedException(State[] allowedInStates) {
		StringBuilder sb = new StringBuilder();
		sb.append("currentState=").append(getCurrentState());
		sb.append(",allowedInState=").append(allowedInStates);
		return new NotAllowedinCurrentStateException(sb.toString());
	}

}

final class CallableExecutorNoTransition extends CallableExecutor {

	CallableExecutorNoTransition(final StateInfo stateInfo) {
		super(stateInfo);
	}

}

final class CallableExecutorStateUpdated extends CallableExecutor {
	
	private final State stateWhenRequestWasAccepted;

	CallableExecutorStateUpdated(final StateInfo stateInfo) {
		super(stateInfo);
		this.stateWhenRequestWasAccepted = getCurrentState();
	}

	public <T> T execute(ICallable<T> callable) {	
		try {
			return super.execute(callable);
		} finally {
			setNewState(stateWhenRequestWasAccepted);	
		}
	}

}

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
