package org.mahu.proto.restapp.model;

/**
 * The ProcessDefinition contains the process ready to execute.
 */
public interface ProcessDefinition {
	
	/**
	 * A process can have children. The top level process is the president.
	 * @return
	 */
	public boolean isPresident();
	
	/**
	 * @return name of the process
	 */
	public String getName();
	
	/**
	 * @return parent of null, if this is the president
	 */
	public ProcessDefinition getParent();
	
	public Node getFirstNode();
	
	public int getNoOfNodes();

	public Node getNode(String nameNextNode);
	
	public ProcessPath getProcessPath(String name);
}
