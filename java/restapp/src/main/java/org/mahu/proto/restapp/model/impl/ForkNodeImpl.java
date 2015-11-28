package org.mahu.proto.restapp.model.impl;

import static org.mahu.proto.restapp.model.impl.ModelAssert.checkArgument;
import static org.mahu.proto.restapp.model.impl.ModelAssert.checkState;

import org.mahu.proto.restapp.engine.impl.JoinTask;
import org.mahu.proto.restapp.model.ForkNode;
import org.mahu.proto.restapp.model.NodeRule;
import org.mahu.proto.restapp.model.ProcessDefinition;
import org.mahu.proto.restapp.model.ProcessTask;

public final class ForkNodeImpl extends NodeImpl implements ForkNode {
	
	private final String[] processPathNames;
	private JoinNodeImpl joinNode;
	
	public ForkNodeImpl(final ProcessDefinition aProcessDefinition, final String aName, final Class<? extends ProcessTask> aCls, final String[] aProcessPathNames) throws ProcessBuilderException {
		super(aProcessDefinition, aName, aCls);
		checkArgument(aProcessPathNames!=null && aProcessPathNames.length >0,"1 or more ProcessPath name arguments required");		
		processPathNames = aProcessPathNames;
	}
	
	public NodeRuleImpl when(final Enum<?> aValue) throws ProcessBuilderException {
		throw new ProcessBuilderException("Method not allowed for this node");
	}

	public NodeRule withJoin(final String aJoinName) throws ProcessBuilderException {
		checkState(joinNode==null, "Only one join allowed for a fork");
		joinNode = new JoinNodeImpl(getProcessDefinition(), aJoinName, JoinTask.class, getName());
		((ProcessDefinitionImpl)getProcessDefinition()).addNode(joinNode);
		// Add Join node to all sub-paths
		for(String processPathName: processPathNames) {
			ProcessPathImpl processPath = (ProcessPathImpl)getProcessDefinition().getProcessPath(processPathName);
			JoinNodeImpl processPathNode = new JoinNodeImpl(joinNode, processPath, aJoinName, JoinTask.class);
			processPathNode.isFinal();
			processPath.addNode(processPathNode);
		}
		return joinNode.when(ProcessTask.Result.Next);
	}

	public JoinNodeImpl getJoinNode() {
		return joinNode;
	}
	
	public String [] getProcessPathNames() {
		return processPathNames.clone();
	}

}
