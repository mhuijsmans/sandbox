package org.mahu.proto.jersey.proxy.statemachine;

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