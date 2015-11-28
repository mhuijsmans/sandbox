package org.mahu.proto.restapp.model.impl;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.mahu.proto.restapp.model.impl.ModelAssert.checkArgumentLengtGtZero;
import static org.mahu.proto.restapp.model.impl.ModelAssert.checkArgumentNotNull;
import static org.mahu.proto.restapp.model.impl.ModelAssert.checkState;

import java.util.LinkedList;
import java.util.List;

import org.mahu.proto.restapp.model.Node;
import org.mahu.proto.restapp.model.ProcessDefinition;
import org.mahu.proto.restapp.model.ProcessPath;

public class ProcessDefinitionImpl implements ProcessDefinition {

	private final String name;
	private NodeImpl firstNode;
	private final List<NodeImpl> nodes = new LinkedList<NodeImpl>();
	private final List<ProcessPath> processPaths = new LinkedList<ProcessPath>();
	private final ProcessDefinitionImpl parent;

	public ProcessDefinitionImpl(final String aName) throws ProcessBuilderException {
		this(null, aName);
	}
	
	public ProcessDefinitionImpl(final ProcessDefinitionImpl aParent, final String aName) throws ProcessBuilderException {
		checkArgumentLengtGtZero(aName);
		name = aName;
		parent = aParent;
	}
	

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Node getFirstNode() {
		return firstNode;
	}
	

	@Override
	public boolean isPresident() {
		return parent==null;
	}

	@Override
	public ProcessDefinition getParent() {
		return parent;
	}	

	public void setFirstNode(final NodeImpl aNode) throws ProcessBuilderException {
		checkArgumentNotNull(aNode);
		checkState(firstNode==null);
		checkNodeNameUnique(aNode);
		firstNode = aNode;
		addNode(aNode);
	}

	public void addNode(final NodeImpl aNode) throws ProcessBuilderException {
		checkArgumentNotNull(aNode);
		checkNodeNameUnique(aNode);
		nodes.add(aNode);
	}
	
	public void addNode2(final NodeImpl aNode) throws ProcessBuilderException {
		if (firstNode==null) {
			setFirstNode(aNode);
		} else {
			addNode(aNode);
		}
	}
	
	public boolean isNodeAlreadyDefined(final NodeImpl node) {
		return getNode(node.getName()) != null;
	}
		
	public void addProcesspath(final ProcessPath aPath) throws ProcessBuilderException {
		checkArgumentNotNull(aPath);
		checkNodeNameUnique(aPath);
		processPaths.add(aPath);
	}
	
	public int getNoOfNodes() {
		return nodes.size();
	}

	List<NodeImpl> getNodes() {
		return nodes;
	}

	@Override
	public Node getNode(final String nameNextTask) {
		checkNotNull(nameNextTask);
		for(Node n : nodes) {
			if (n.getName().equals(nameNextTask)) {
				return n;
			}
		}
		return null;
	}
	
	@Override
	public ProcessPath getProcessPath(final String name) {
		checkNotNull(name);
		for(ProcessPath n : processPaths) {
			if (n.getName().equals(name)) {
				return n;
			}
		}
		return null;
	}		
	
	@Override
	public boolean equals(final Object o) {
		if (o==null) {
			return false; 
		}
		ProcessDefinitionImpl pb = (ProcessDefinitionImpl)o;
		return pb.name.equals(name);	
	}
	
	private void checkNodeNameUnique(final NodeImpl node)
			throws ProcessBuilderException {
		checkArgumentNotNull(node);
		if (getNode(node.getName()) != null) {
			throw new ProcessBuilderException("Node name already defined: "
					+ node.getName());
		}
	}
	
	private void checkNodeNameUnique(final ProcessPath aPath)
			throws ProcessBuilderException {
		checkArgumentNotNull(aPath);
		if (getProcessPath(aPath.getName()) != null) {
			throw new ProcessBuilderException("Processpath name already defined: "
					+ aPath.getName());
		}
	}

}
