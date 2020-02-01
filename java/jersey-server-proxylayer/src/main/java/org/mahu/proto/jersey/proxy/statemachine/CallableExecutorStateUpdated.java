package org.mahu.proto.jersey.proxy.statemachine;

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