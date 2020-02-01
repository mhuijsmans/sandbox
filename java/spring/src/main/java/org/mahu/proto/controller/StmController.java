package org.mahu.proto.controller;

import org.mahu.proto.stm.Event;
import org.mahu.proto.stm.IStateMachine;
import org.mahu.proto.stm.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest")
public class StmController {
	
	private final IStateMachine stateMachine;
	
	@Autowired
	StmController(final IStateMachine stateMachine) {
		this.stateMachine = stateMachine;
	}
	
	@GetMapping(value="/value", produces = "text/plain")
	public String getValueXml() {
		return stateMachine.ifState(State.IDLE).thenTransitionTo(State.BUSY).registerEvent(Event.X).andCall(
				() -> getValueXmlImpl());
	}
	
	private String getValueXmlImpl() {
		return "my-state";
	}	

}
