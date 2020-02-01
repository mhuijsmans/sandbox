package org.mahu.proto.jersey.proxy.statemachine;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Stm { 
	// State in which request is allowed 
    public State[] allowedInStates();
    // The transitionToState can be empty or contain a single value. It is an error to assign more than one value.
	// State during request execution, on completion the state machine returned to the state before execution.    
    public TransitionToState transitionToState();
}
