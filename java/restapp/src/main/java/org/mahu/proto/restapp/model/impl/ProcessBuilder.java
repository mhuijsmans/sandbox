package org.mahu.proto.restapp.model.impl;

import static org.mahu.proto.restapp.model.impl.ModelAssert.checkArgument;
import static org.mahu.proto.restapp.model.impl.ModelAssert.checkArgumentLengtGtZero;
import static org.mahu.proto.restapp.model.impl.ModelAssert.checkState;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mahu.proto.restapp.engine.impl.ForkTask;
import org.mahu.proto.restapp.model.ForkNode;
import org.mahu.proto.restapp.model.Node;
import org.mahu.proto.restapp.model.ProcessDefinition;
import org.mahu.proto.restapp.model.ProcessTask;
import org.mahu.proto.restapp.task.EndTask;

/**
 * This class support the creation of ProcessDefinition.
 */
public class ProcessBuilder {

	final static Logger logger = LogManager.getLogger(ProcessBuilder.class.getName());

	protected final ProcessDefinitionImpl processDef;
	private final List<ProcessPathBuilder> paths = new LinkedList<ProcessPathBuilder>();
	protected final ProcessBuilder parent;

	public ProcessBuilder(final String name) throws ProcessBuilderException {
		this(name, null);
	}

	public ProcessBuilder(final String name, final ProcessBuilder aParent)
			throws ProcessBuilderException {
		checkArgumentLengtGtZero(name);
		parent = aParent;
		processDef = createNewProcessDefinition(name);
	}

	public String getName() throws ProcessBuilderException {
		checkState(processDef != null);
		return processDef.getName();
	}

	public boolean hasParent() {
		return parent != null;
	}

	public Node getNode(final String name) throws ProcessBuilderException {
		return processDef.getNode(name);
	}

	/**
	 * addTask2 combines first/addTask in one.
	 * 
	 * @param name
	 * @param cls
	 * @return
	 * @throws ProcessBuilderException
	 */
	public Node addTask2(final String name,
			final Class<? extends ProcessTask> cls)
			throws ProcessBuilderException {
		NodeImpl node = new NodeImpl(processDef, name, cls);
		processDef.addNode2(node);
		return node;
	}

	public Node addTask(final String name,
			final Class<? extends ProcessTask> cls)
			throws ProcessBuilderException {
		NodeImpl node = new NodeImpl(processDef, name, cls);
		processDef.addNode(node);
		return node;
	}

	public Node firstTask(final String name,
			final Class<? extends ProcessTask> cls)
			throws ProcessBuilderException {
		NodeImpl node = new NodeImpl(processDef, name, cls);
		processDef.setFirstNode(node);
		return node;
	}

	public ProcessDefinition buildProcessDefinition()
			throws ProcessBuilderException {
		checkProcessModel();
		return processDef;
	}

	public ProcessPathBuilder createProcessPath(final String name)
			throws ProcessBuilderException {
		checkArgumentLengtGtZero(name);
		checkState(processDef != null,
				"Path can only be defined after process name has been set");
		String internalname = processDef.getName() + "." + name;
		checkProcessPathNameIsUnique(internalname);
		ProcessPathBuilder path = new ProcessPathBuilder(internalname, this);
		paths.add(path);
		processDef.addProcesspath(path.getProcessPath());
		return path;
	}

	/**
	 * Add a Fork node for the defined set of processPaths. Add a Join node, to
	 * which ProcessPaths can connect, when ready
	 * 
	 * @param nameForkNode
	 * @param processPaths
	 * @param nameJoinNode
	 * @return
	 * @throws ProcessBuilderException
	 */
	public ForkNode addFork(final String nameForkNode,
			final ProcessPathBuilder[] processPaths)
			throws ProcessBuilderException {
		checkArgument(processPaths != null && processPaths.length > 0,
				"1 or more ProcessPathBuilder arguments required");
		checkProcessPathsExistAndAreUnique(processPaths);
		checkProcessPathsAreNotUsedYet(processPaths);
		String[] processPathNames = createProcessPathNameArray(processPaths);
		ForkNodeImpl node = new ForkNodeImpl(processDef, nameForkNode,
				ForkTask.class, processPathNames);
		node.isFinal();
		processDef.addNode(node);
		return node;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null) {
			return false;
		}
		ProcessBuilder pb = (ProcessBuilder) o;
		try {
			return pb.getName().equals(getName());
		} catch (ProcessBuilderException e) {
			return false;
		}
	}

	protected ProcessDefinitionImpl createNewProcessDefinition(final String name)
			throws ProcessBuilderException {
		return new ProcessDefinitionImpl(name);
	}

	protected void checkProcessModel() throws ProcessBuilderException {
		if (processDef == null) {
			throw new ProcessBuilderException("Incomplete process definition");
		}
		// Check that there is a first node
		checkThatFirstNodeIsDefined();
		// Check that for all nodes, all results are defined
		checkThatForAllNodesAllResultsAreDefined();
		// Check that for all nodes, the nextNode names exists
		checkThatForAllNodesTheNextNodeExists();
		// Check that all included nodes are reachable
		checkThatAllNodesAreReachable();
		// Check that all processPaths are used
		checkAllProcessPathsAreUsed();
		// Check that all subprocesses are valid
		checkThatAllProcessPathsAreValid();
	}

	protected void checkThatAllNodesAreReachable()
			throws ProcessBuilderException {
		final Set<String> nodeSet = new HashSet<String>();
		addNodesToSet(nodeSet, (NodeImpl) processDef.getFirstNode());
		if (nodeSet.size() != processDef.getNoOfNodes()) {
			throw new ProcessBuilderException(
					"Not all nodes of "
							+ processDef.getName()
							+ " are reachable from first, i.e. there are orphaned tasks; used: "
							+ nodeSet.size() + " present: "
							+ processDef.getNoOfNodes());
		}
	}

	protected void checkThatForAllNodesTheNextNodeExists()
			throws ProcessBuilderException {
		for (NodeImpl n : processDef.getNodes()) {
			if (n instanceof ForkNodeImpl) {
				continue;
			}
			for (NodeRuleImpl r : n.getRules()) {
				if (r.getNameNextTask() == null) {
					throw new ProcessBuilderException("Node " + n.getName()
							+ " has not connected " + r.getValue()
							+ ", next(..) missing");
				}
				if (processDef.getNode(r.getNameNextTask()) == null) {
					throw new ProcessBuilderException("Node " + n.getName()
							+ " has connected " + r.getValue()
							+ " non-existing to " + r.getNameNextTask());
				}
			}
		}
	}

	protected void checkThatForAllNodesAllResultsAreDefined()
			throws ProcessBuilderException {
		for (NodeImpl n : processDef.getNodes()) {
			// If ProcessTask has own results, check that all results are used.
			if (n instanceof ForkNodeImpl) {
				continue;
			}
			if (n.getResult().length > 0
					&& n.getRules().size() != n.getResult().length) {
				throw new ProcessBuilderException("Not all results for node "
						+ n.getName() + " are connected, #rules="
						+ n.getRules().size() + ", #results="
						+ n.getResult().length);
			}
		}
	}

	protected void checkThatFirstNodeIsDefined() throws ProcessBuilderException {
		if (processDef.getFirstNode() == null) {
			if (processDef.getNoOfNodes() == 0) {
				// Special case
				firstTask("first", EndTask.class).isFinal();
			} else {
				throw new ProcessBuilderException(
						"First task missing for process "
								+ processDef.getName());
			}
		}
	}

	private void checkAllProcessPathsAreUsed() throws ProcessBuilderException {
		for (ProcessPathBuilder p1 : paths) {
			boolean found = false;
			for (Node n : processDef.getNodes()) {
				if (n instanceof ForkNodeImpl) {
					ForkNodeImpl fn = (ForkNodeImpl) n;
					for (String processPathName : fn.getProcessPathNames()) {
						if (p1.getName().equals(processPathName)) {
							found = true;
						}
					}
				}
			}
			if (!found) {
				throw new ProcessBuilderException("ProcessPath is not used "
						+ p1);
			}
		}
	}

	private void checkThatAllProcessPathsAreValid()
			throws ProcessBuilderException {
		for (ProcessPathBuilder p : paths) {
			p.checkProcessModel();
		}
	}

	private void addNodesToSet(Set<String> nodeSet, final NodeImpl node) {
		// logger.debug("Adding node {}", node.getName());
		nodeSet.add(node.getName());
		for (NodeRuleImpl r : node.getRules()) {
			addNodesToSet(nodeSet,
					(NodeImpl) processDef.getNode(r.getNameNextTask()));
		}
		if (node instanceof ForkNodeImpl) {
			ForkNodeImpl fn = (ForkNodeImpl) node;
			if (fn.getJoinNode() != null) {
				addNodesToSet(nodeSet, fn.getJoinNode());
			}
		}
	}

	private void checkProcessPathsExistAndAreUnique(
			final ProcessPathBuilder[] processPaths)
			throws ProcessBuilderException {
		for (ProcessPathBuilder p : processPaths) {
			if (!paths.contains(p)) {
				throw new ProcessBuilderException("Unknown ProcessPath "
						+ p.getName());
			}
		}
		for (int i = 0; i < processPaths.length; i++) {
			for (int j = i + 1; j < processPaths.length; j++) {
				if (processPaths[i] == processPaths[j]) {
					throw new ProcessBuilderException("Duplicate path "
							+ processPaths[i].getName());
				}
			}
		}
	}

	private void checkProcessPathsAreNotUsedYet(
			final ProcessPathBuilder[] processPaths)
			throws ProcessBuilderException {
		for (Node n : processDef.getNodes()) {
			// If ProcessTask has own results, check that all results are used.
			if (n instanceof ForkNodeImpl) {
				ForkNodeImpl fn = (ForkNodeImpl) n;
				for (String processPathName : fn.getProcessPathNames()) {
					for (ProcessPathBuilder p2 : processPaths) {
						if (processPathName.equals(p2.getName())) {
							throw new ProcessBuilderException(
									"ProcessPath already used " + p2);
						}
					}
				}
			}
		}
	}

	private String[] createProcessPathNameArray(
			final ProcessPathBuilder[] processPaths)
			throws ProcessBuilderException {
		String[] names = new String[processPaths.length];
		int i = 0;
		for (ProcessPathBuilder p : processPaths) {
			names[i++] = p.getName();
		}
		return names;
	}

	private void checkProcessPathNameIsUnique(final String internalname)
			throws ProcessBuilderException {
		for (ProcessPathBuilder p : paths) {
			if (p.getName().equals(internalname)) {
				throw new ProcessBuilderException(
						"ProcessPath name is not unique " + internalname);
			}
		}
	}

}
