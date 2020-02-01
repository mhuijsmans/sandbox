package org.mahu.proto.stm;

import org.springframework.stereotype.Component;

@Component
public class StateMachine implements  IStateMachine {

	@Override
	public IStmEvent ifState(State... requiredState) {
		// TODO Auto-generated method stub
		return new StmEvent(requiredState);
	}

}
