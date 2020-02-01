package org.mahu.proto.stm;

public interface IStateMachine {

	IStmEvent ifState(State... requiredState);

}
