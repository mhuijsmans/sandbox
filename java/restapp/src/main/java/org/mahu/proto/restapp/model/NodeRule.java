package org.mahu.proto.restapp.model;

import org.mahu.proto.restapp.model.impl.ProcessBuilderException;

public interface NodeRule {
	
	public Node next(final String name) throws ProcessBuilderException;
	
	public Enum<?> getValue();
	public String getNameNextTask();	

}
