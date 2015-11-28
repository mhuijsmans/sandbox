package org.mahu.proto.restapp.model;

import org.mahu.proto.restapp.model.impl.ProcessBuilderException;

public interface ForkNode extends Node {
	
	public NodeRule withJoin(final String aJoinName) throws ProcessBuilderException;	

}
