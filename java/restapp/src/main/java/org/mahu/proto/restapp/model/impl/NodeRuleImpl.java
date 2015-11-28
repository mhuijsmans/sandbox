package org.mahu.proto.restapp.model.impl;

import static org.mahu.proto.restapp.model.impl.ModelAssert.*;

import org.mahu.proto.restapp.model.NodeRule;

class NodeRuleImpl implements NodeRule {
	private final NodeImpl node;
	private final Enum<?> value;
	private final SetOnceString nameNextTask = new SetOnceString();

	NodeRuleImpl(final NodeImpl aNode, final Enum<?> aValue) throws ProcessBuilderException {
		checkArgumentNotNull(aNode);
		checkArgumentNotNull(aValue);
		node = aNode;
		value = aValue;
	}

	public NodeImpl next(final String name) throws ProcessBuilderException {
		checkArgumentLengtGtZero(name);
		checkArgument(!node.getName().equals(name));
		nameNextTask.set(name);
		return node;
	}
	
	public Enum<?> getValue() {
		return value;
	}

	public String getNameNextTask() {
		return nameNextTask.getValue();
	}
}