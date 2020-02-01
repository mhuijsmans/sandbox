package org.mahu.proto.jersey.proxy.statemachine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Optional;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class StateControllerTest 
{
	private static final String HI = "hi";
	
	static class TestException extends RuntimeException {
		
	}
	
	static class Request implements ICallable<String>{
		
		private final IStateController stateController;
		private Optional<State> stateDuringExecution = Optional.empty();
		
		Request(IStateController stateController) {
			this.stateController = stateController;
		}

		public String execute() {
			stateDuringExecution = Optional.of(stateController.getState());
			return HI;
		}

		public Optional<State> getStateDuringExecution() {
			return stateDuringExecution;
		}

	}	
	
	static class RequestThrowsException implements ICallable<String>{
		
		public String execute() {
			throw new TestException();
		}


	}	
	
	static class RequestThrowsFatalException implements ICallable<String>{
		
		public String execute() {
			throw new FatalException();
		}


	}	
	
	@Stm(allowedInStates= {State.IDLE}, transitionToState=TransitionToState.NO_TRANSITION_NON_BLOCKING)
	static class RequestNoTransition extends Request {
		
		RequestNoTransition(IStateController stateController) {
			super(stateController);
		}

	}
	
	@Stm(allowedInStates= {State.IDLE}, transitionToState=TransitionToState.STATE_BUSY)
	static class RequestWithTransition extends Request {
		
		RequestWithTransition(IStateController stateController) {
			super(stateController);
		}

	}
	
	@Stm(allowedInStates= {State.IDLE}, transitionToState=TransitionToState.NO_TRANSITION_NON_BLOCKING)
	static class RequestNoTransitionThrowsException extends RequestThrowsException {

	}
	
	@Stm(allowedInStates= {State.IDLE}, transitionToState=TransitionToState.STATE_BUSY)
	static class RequestWithTransitionThrowsException extends RequestThrowsException {

	}
	
	@Stm(allowedInStates= {State.IDLE}, transitionToState=TransitionToState.NO_TRANSITION_NON_BLOCKING)
	static class RequestNoTransitionThrowsFatalException extends RequestThrowsFatalException {

	}
	
	@Stm(allowedInStates= {State.IDLE}, transitionToState=TransitionToState.STATE_BUSY)
	static class RequestWithTransitionThrowsFatalException extends RequestThrowsFatalException {

	}	
		
    @Test
    public void execute_requestNoTransitionAllowedInState_correctStateDuringAndAfterExecution()
    {
    	IStateController stateController = new StateController(State.IDLE);
        
    	Request request = new RequestNoTransition(stateController);
		String result = stateController.execute(request);
    	
    	assertNotNull(HI, result);
    	assertEquals(State.IDLE, request.getStateDuringExecution().get());
    	assertEquals(State.IDLE, stateController.getState());
    }
    
    @Test
    public void execute_requestWithTransitionAllowedInState_correctStateDuringAndAfterExecution()
    {
    	IStateController stateController = new StateController(State.IDLE);
        
    	Request request = new RequestWithTransition(stateController);
		String result = stateController.execute(request);
    	
    	assertNotNull(HI, result);
    	assertEquals(State.BUSY, request.getStateDuringExecution().get());
    	assertEquals(State.IDLE, stateController.getState());
    }    
    
    @Test
    public void execute_requestNoTransitionNotAllowedInState_correctStateAfterExecution()
    {
    	IStateController stateController = new StateController(State.BUSY);
        
    	Request request = new RequestNoTransition(stateController);
    	try {
    		stateController.execute(request);
    		fail("Expected Exception ");
    	} catch(NotAllowedinCurrentStateException e) {
    		// expected exception
    	}
    	
    	assertEquals(State.BUSY, stateController.getState());
    	assertFalse(request.getStateDuringExecution().isPresent());    	
    }
    
    @Test
    public void execute_requestWithTransitionNotAllowedInState_correctStateAfterExecution()
    {
    	IStateController stateController = new StateController(State.BUSY);
        
    	Request request = new RequestWithTransition(stateController);
    	try {
    		stateController.execute(request);
    		fail("Expected Exception ");
    	} catch(NotAllowedinCurrentStateException e) {
    		// expected exception
    	}
    	
    	assertEquals(State.BUSY, stateController.getState());  	
    }
    
    @Test
    public void execute_requestNoTransitionThrowsException_correctStateDuringAndAfterExecution()
    {
    	IStateController stateController = new StateController(State.IDLE);
        
    	RequestThrowsException request = new RequestNoTransitionThrowsException();
    	try {
    		stateController.execute(request);
    		fail("Expected Exception ");
    	} catch(TestException e) {
    		// expected exception
    	}
    	assertEquals(State.IDLE, stateController.getState());
    }
    
    @Test
    public void execute_requestWithTransitionThrowsException_correctStateDuringAndAfterExecution()
    {
    	IStateController stateController = new StateController(State.IDLE);
        
    	RequestThrowsException request = new RequestWithTransitionThrowsException();
    	try {
    		stateController.execute(request);
    		fail("Expected Exception ");
    	} catch(TestException e) {
    		// expected exception
    	}
    	
    	assertEquals(State.IDLE, stateController.getState());
    }     
    
    @Test
    public void execute_requestNoTransitionThrowsFatalException_correctStateDuringAndAfterExecution()
    {
    	IStateController stateController = new StateController(State.IDLE);
        
    	RequestThrowsFatalException request = new RequestNoTransitionThrowsFatalException();
    	try {
    		stateController.execute(request);
    		fail("Expected Exception ");
    	} catch(FatalException e) {
    		// expected exception
    	}
    	assertEquals(State.FATAL, stateController.getState());
    }
    
    @Test
    public void execute_requestWithTransitionThrowsFatalException_correctStateDuringAndAfterExecution()
    {
    	IStateController stateController = new StateController(State.IDLE);
        
    	RequestThrowsFatalException request = new RequestWithTransitionThrowsFatalException();
    	try {
    		stateController.execute(request);
    		fail("Expected Exception ");
    	} catch(FatalException e) {
    		// expected exception
    	}
    	
    	assertEquals(State.FATAL, stateController.getState());
    }     

}
