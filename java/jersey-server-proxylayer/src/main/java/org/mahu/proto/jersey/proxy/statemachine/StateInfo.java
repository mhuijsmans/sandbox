package org.mahu.proto.jersey.proxy.statemachine;

public class StateInfo {
	
	private State currentState;

	public StateInfo(State initialState) {
		currentState = initialState;
	}

	public State getCurrentState() {
		return currentState;
	}

	public void setNewState(State newState) {
		if (currentState != State.FATAL) {
			currentState = newState;
		}
	}

}
