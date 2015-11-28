package org.mahu.proto.restapp.model.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;

import org.mahu.proto.restapp.model.ProcessAlreadyExistsException;
import org.mahu.proto.restapp.model.ProcessDefinition;
import org.mahu.proto.restapp.model.ProcessDefinitionRepo;

public class ProcessDefinitionRepoImpl implements ProcessDefinitionRepo {
	
	protected final Map<String, ProcessDefinition> processes = new HashMap<String, ProcessDefinition>();

	@Override
	public ProcessDefinition getProcess(String processName) {
		checkNotNull(processName);
		return processes.get(processName);
	}

	@Override
	public void addProcess(ProcessDefinition processDefinition) throws ProcessAlreadyExistsException {
		checkNotNull(processDefinition);
		if (processes.containsKey(processDefinition.getName())) {
			throw new ProcessAlreadyExistsException("Process already exists: " +processDefinition.getName());
		}
		processes.put(processDefinition.getName(), processDefinition);
	} 

}
