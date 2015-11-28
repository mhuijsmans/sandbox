package org.mahu.proto.restapp.model.impl;

import org.mahu.proto.restapp.model.JoinNode;
import org.mahu.proto.restapp.model.ProcessDefinition;
import org.mahu.proto.restapp.model.ProcessTask;

public class JoinNodeImpl extends NodeImpl implements JoinNode {

	private final String forkName;
	private final JoinNodeImpl parent;

	JoinNodeImpl(final ProcessDefinition aProcessDefinition, final String aName,
			final Class<? extends ProcessTask> aCls, final String aForkName)
			throws ProcessBuilderException {
		super(aProcessDefinition, aName, aCls);
		parent = null;
		forkName = aForkName;
	}

	JoinNodeImpl(final JoinNodeImpl aParent,
			final ProcessDefinition aProcessDefinition, final String aName,
			final Class<? extends ProcessTask> aCls)
			throws ProcessBuilderException {
		super(aProcessDefinition, aName, aCls);
		parent = aParent;
		forkName = null;
	}

	public JoinNodeImpl getParent() {
		return parent;
	}
	
	public String getForkName() {
		return forkName;
	}

}
