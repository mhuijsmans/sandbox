package org.mahu.proto.restapp.model;


/**
 * The process repository holds the process definitions.
 */
public interface ProcessDefinitionRepo {

	public void addProcess(final ProcessDefinition processDefinition) throws ProcessAlreadyExistsException;

	public ProcessDefinition getProcess(final String processName);
}
