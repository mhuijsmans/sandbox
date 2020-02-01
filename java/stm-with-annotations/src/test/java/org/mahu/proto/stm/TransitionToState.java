package org.mahu.proto.stm;

import java.util.Optional;

public enum TransitionToState {
	// no update state as a result of current state, non-blocking call, i.e. new rest calls are allowed
	// A new request can result in a state transition 
	NO_TRANSITION_NON_BLOCKING,
	// transition to state busy, if the request if accepted.
	// Once the request is completed, it automatically returns to the idle state.
	STATE_BUSY(State.BUSY);
	
	private final Optional<State> newState;	
	
	TransitionToState() {
		newState = Optional.empty();
	}
	
	TransitionToState(State newState) {
		this.newState = Optional.of(newState);
	}	
	
	Optional<State> getNewState() {
		return newState;
	}
}

