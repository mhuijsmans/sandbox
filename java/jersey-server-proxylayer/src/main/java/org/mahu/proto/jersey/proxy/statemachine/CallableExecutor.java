package org.mahu.proto.jersey.proxy.statemachine;

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