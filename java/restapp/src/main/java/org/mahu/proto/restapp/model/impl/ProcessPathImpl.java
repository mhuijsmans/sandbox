package org.mahu.proto.restapp.model.impl;

import org.mahu.proto.restapp.model.ProcessPath;

public class ProcessPathImpl extends ProcessDefinitionImpl implements ProcessPath {
	
	public ProcessPathImpl(final ProcessDefinitionImpl parent, final String aName) throws ProcessBuilderException {
		super(parent,aName);
	}

}
