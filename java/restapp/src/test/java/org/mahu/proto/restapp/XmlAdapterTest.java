package org.mahu.proto.restapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mahu.proto.forkprocesstest.ProcessManager;
import org.mahu.proto.restapp.engine.SessionId;
import org.mahu.proto.restapp.engine.SessionIdManager;
import org.mahu.proto.restapp.engine.WorkflowEngine;
import org.mahu.proto.restapp.engine.WorkflowEngine.State;
import org.mahu.proto.restapp.engine.WorkflowEngineException;
import org.mahu.proto.restapp.engine.impl.WorkflowEngineImpl;
import org.mahu.proto.restapp.model.ProcessAlreadyExistsException;
import org.mahu.proto.restapp.model.ProcessDefinition;
import org.mahu.proto.restapp.model.ProcessDefinitionRepo;
import org.mahu.proto.restapp.model.impl.ProcessBuilderException;
import org.mahu.proto.restapp.model.impl.ProcessDefinitionRepoImpl;
import org.mahu.proto.restapp.service.Service;
import org.mahu.proto.restapp.util.ReflectionUtil.ReflectionUtilException;
import org.mahu.proto.restapp.xmladapter.ActivitiBmpn2XmlReader;

public class XmlAdapterTest {

	private SessionId id;
	private WorkflowEngine engine;
	private ProcessDefinitionRepo processDefinitionRepo;

	@Before
	public void RunBeforeEveryTest() throws ProcessBuilderException,
			ProcessAlreadyExistsException {
		processDefinitionRepo = new ProcessDefinitionRepoImpl();
		engine = new WorkflowEngineImpl();
		id = SessionIdManager.Create();
	}

	@After
	public void RunAfterEveryTest() {
	}

	// TODO: execution time test case is too long. Error or wromg implementation?
	@Test(timeout = 8000)
	public void ReadAndExecuteBPMN() throws WorkflowEngineException,
			UnsupportedEncodingException, IOException, URISyntaxException,
			XMLStreamException, ReflectionUtilException, InterruptedException {
		java.net.URL url = getClass().getClassLoader().getResource(
				"proc2.bpmn20.xml");
		assertTrue(url != null);
		java.nio.file.Path resPath = java.nio.file.Paths.get(url.toURI());
		String xml = new String(java.nio.file.Files.readAllBytes(resPath),
				"UTF8");
		assertTrue(xml.length() > 0);

		ActivitiBmpn2XmlReader adapter = new ActivitiBmpn2XmlReader();
		adapter.readXml(xml);
		ProcessDefinition processDefinition = adapter.convertLoadedModel();
		processDefinitionRepo.addProcess(processDefinition);
		//
		final Map<String, Object> data = new HashMap<>();
		ProcessManager processManager = new ProcessManager(4);
		data.put("testservice", new Service(processManager));
		data.put(ProcessManager.class.getName(), processManager);
		// test
		engine.Init(id, processDefinition, data, null);
		State state = engine.ExecuteJobsUntilFinalStateReached(4000);
		processManager.StopProcesses();
		// verify
		assertEquals(State.TERMINATED, state);
	}

}
