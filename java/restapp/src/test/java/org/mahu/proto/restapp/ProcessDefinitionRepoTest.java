package org.mahu.proto.restapp;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mahu.proto.restapp.engine.WorkflowEngineException;
import org.mahu.proto.restapp.model.ProcessAlreadyExistsException;
import org.mahu.proto.restapp.model.ProcessDefinitionRepo;
import org.mahu.proto.restapp.model.impl.ProcessBuilder;
import org.mahu.proto.restapp.model.impl.ProcessBuilderException;
import org.mahu.proto.restapp.model.impl.ProcessDefinitionRepoImpl;

public class ProcessDefinitionRepoTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	private ProcessDefinitionRepo processDefinitionRepo;

	@Before
	public void RunBeforeEveryTest() throws ProcessBuilderException,
			ProcessAlreadyExistsException {
		processDefinitionRepo = new ProcessDefinitionRepoImpl();
	}

	@After
	public void RunAfterEveryTest() {
	}

	@Test
	public void errorNoProcessname() throws WorkflowEngineException {
		// test
		exception.expect(NullPointerException.class);
		processDefinitionRepo.addProcess(null);
	}

	@Test
	public void errorProcessExists() throws WorkflowEngineException {
		// Preparation
		ProcessBuilder pb = new ProcessBuilder("process1");
		processDefinitionRepo.addProcess(pb.buildProcessDefinition());
		// test
		exception.expect(ProcessAlreadyExistsException.class);
		processDefinitionRepo.addProcess(pb.buildProcessDefinition());
	}

	@Test
	public void addProcess() throws WorkflowEngineException {
		// Preparation
		ProcessBuilder pb = new ProcessBuilder("process1");
		// test
		processDefinitionRepo.addProcess(pb.buildProcessDefinition());
	}
}
