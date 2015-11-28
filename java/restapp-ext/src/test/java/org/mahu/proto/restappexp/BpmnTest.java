package org.mahu.proto.restappexp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mahu.proto.commons.StopWatch;
import org.mahu.proto.restapp.asyntask.AsyncTaskManager;
import org.mahu.proto.restapp.asyntask.AsyncTaskManagerImpl;
import org.mahu.proto.restapp.engine.SessionId;
import org.mahu.proto.restapp.engine.SessionIdManager;
import org.mahu.proto.restapp.engine.WorkflowEngine;
import org.mahu.proto.restapp.engine.WorkflowEngine.State;
import org.mahu.proto.restapp.engine.WorkflowEngineException;
import org.mahu.proto.restapp.engine.impl.WorkflowEngineImpl;
import org.mahu.proto.restapp.model.ProcessAlreadyExistsException;
import org.mahu.proto.restapp.model.ProcessDefinition;
import org.mahu.proto.restapp.model.impl.ProcessBuilderException;
import org.mahu.proto.restapp.util.ReflectionUtil.ReflectionUtilException;
import org.mahu.proto.restapp.xmladapter.ActivitiBmpn2XmlReader;
import org.mahu.proto.restappexp.IntegrationTest.VeInterfaceMock;
import org.mahu.proto.restappexp.service.VEInterface;
import org.mahu.proto.restappext.task.BCChoiceTask;
/**
 * The IntegrationTest
 */
public class BpmnTest {

	final static Logger logger = LogManager.getLogger(BpmnTest.class.getName());

	private StringBuffer report;

	private AsyncTaskManagerImpl asyncTaskManager;
	private SessionId id;
	private final WorkflowEngine.Listener nullObserver = null;
	private VEInterface veInterface;
	private ProcessDefinition processDefinition;
	private Map<String, Object> data;
	private StopWatch sw1;

	@Before
	public void RunBeforeEveryTest() throws ProcessBuilderException,
			ProcessAlreadyExistsException, URISyntaxException,
			UnsupportedEncodingException, IOException, XMLStreamException,
			ReflectionUtilException {
		asyncTaskManager = new AsyncTaskManagerImpl(3);
		id = SessionIdManager.Create();
		report = new StringBuffer();
		// Start Services
		veInterface = new VeInterfaceMock();
		java.net.URL url = BpmnTest.class.getClassLoader().getResource(
				TestConst.TEST_SCRIPT);
		assertTrue(url != null);
		java.nio.file.Path resPath = java.nio.file.Paths.get(url.toURI());
		String xml = new String(java.nio.file.Files.readAllBytes(resPath),
				"UTF8");
		assertTrue(xml.length() > 0);

		// Read XML
		sw1 = new StopWatch();
		ActivitiBmpn2XmlReader adapter = new ActivitiBmpn2XmlReader();
		adapter.readXml(xml);
		processDefinition = adapter.convertLoadedModel();
		sw1.Clock();
		//
		// Set input data
		data = new HashMap<>();
		data.put(VEInterface.class.getName(), veInterface);
		data.put(BCChoiceTask.BCCHOICEVAR, BCChoiceTask.QUERY);
		data.put(AsyncTaskManager.class.getName(), asyncTaskManager);
	}

	@After
	public void RunAfterEveryTest() {
		asyncTaskManager.cancelAndClear();
	}

	@Test(timeout = 12000)
	public void ReadAndExecuteBPMN() throws InterruptedException {
		ExecuteWorkflow();
	}

	@Test(timeout = 8000)
	public void ReadAndExecuteBPMN_xtimes() throws WorkflowEngineException,
			UnsupportedEncodingException, IOException, URISyntaxException,
			XMLStreamException, ReflectionUtilException, InterruptedException {
		report.append("=========================================================\n");
		for (int i = 0; i < 10; i++) {
			ExecuteWorkflow();
		}
		logger.info(report.toString());
	}

	private void ExecuteWorkflow() throws InterruptedException {
		// test
		StopWatch sw2 = new StopWatch();
		WorkflowEngine engine = new WorkflowEngineImpl();
		engine.Init(id, processDefinition, data, nullObserver);
		State state = engine.ExecuteJobsUntilFinalStateReached(4000);
		// verify
		assertEquals(State.TERMINATED, state);
		sw2.Clock();
		report.append("XML (in memory) converted in time(ms)=")
				.append(sw1.ElapsedTimeInMS()).append("\n")
				.append("Process executed in time(ms)=")
				.append(sw2.ElapsedTimeInMS()).append("\n");
		logger.info("XML (in memory) converted in time(ms)="
				+ sw1.ElapsedTimeInMS());
		logger.info("Process executed in time(ms)=" + sw2.ElapsedTimeInMS());
	}

}
