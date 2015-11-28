package org.mahu.proto.restapp.model;

import java.util.Enumeration;

import org.mahu.proto.restapp.model.impl.ProcessBuilderException;

// TODO: Node shall e split into Node and NodeConstruction.
// Node provides the runtime view.
// NodeConstruction (extends Node) provides the design time view.
public interface Node {

	public String getName();	
	public ProcessDefinition getProcessDefinition();
	
	public NodeRule when(final Enum<?> aValue) throws ProcessBuilderException;	
	public void isFinal() throws ProcessBuilderException;
	
	public Class<? extends ProcessTask> getProcessTaskClass();
	
	public int getNrOfResults();
	
	public int getNrOfNodeRules();	
	public NodeRule getNodeRule(final int idx);
	public NodeRule findRule(final Enum<?> result);
	public Enumeration<NodeRule> nodeRuleEnumeration();
}
