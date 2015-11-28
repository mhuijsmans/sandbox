package org.mahu.proto.jbpm6;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collection;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.runtime.manager.context.EmptyContext;

import bitronix.tm.resource.jdbc.PoolingDataSource;

public class AppTest {
	private static PoolingDataSource pds;
	private ProcessEngineService processService;

	@BeforeClass
	public static void setupOnce() {
		pds = new PoolingDataSource();
		pds.setUniqueName("jdbc/jbpm-ds");
		pds.setClassName("bitronix.tm.resource.jdbc.lrc.LrcXADataSource");
		pds.setMaxPoolSize(5);
		pds.setAllowLocalTransactions(true);
		pds.getDriverProperties().put("user", "sa");
		pds.getDriverProperties().put("password", "");
		pds.getDriverProperties().put("url", "jdbc:h2:mem:jbpm-db;MVCC=true");
		pds.getDriverProperties().put("driverClassName", "org.h2.Driver");
		pds.init();
	}

	@AfterClass
	public static void cleanup() {
		if (pds != null) {
			pds.close();
		}
	}

	@Before
	public void prepare() {
		cleanupSingletonSessionId();
		processService = new ProcessEngineService();
		processService.init();
	}

	@After
	public void dispose() {
		processService.dispose();
	}

	@Test
	public void customerTaskProcessTest() {

		assertNotNull(processService);

		Collection<org.kie.api.definition.process.Process> processes = processService
				.getProcesses();
		assertNotNull(processes);
		assertEquals(2, processes.size());

		RuntimeManager manager = processService.getRuntimeManager();
		assertNotNull(manager);

		RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
		assertNotNull(engine);

		KieSession ksession = engine.getKieSession();
		assertNotNull(ksession);

		ProcessInstance instance = ksession.startProcess("customtask");
		assertNotNull(instance);
		assertTrue(instance.getState() == ProcessInstance.STATE_COMPLETED);
	}

	@Test
	public void userTaskProcessTest() {

		assertNotNull(processService);

		Collection<org.kie.api.definition.process.Process> processes = processService
				.getProcesses();
		assertNotNull(processes);
		assertEquals(2, processes.size());

		RuntimeManager manager = processService.getRuntimeManager();
		assertNotNull(manager);

		RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
		assertNotNull(engine);

		KieSession ksession = engine.getKieSession();
		assertNotNull(ksession);
		
		TaskService taskService = engine.getTaskService();
		assertNotNull(taskService);

		org.kie.api.runtime.process.ProcessInstance processInstance = ksession
				.startProcess("org.jbpm.writedocument");

		processInstance = checkProcessIsStillActive(ksession, processInstance);

		String actorId = "salaboy";		
		completeTaskForActor(taskService, actorId);
		processInstance = checkProcessIsStillActive(ksession, processInstance);

		String actorIdTranslator = "translator";
		completeTaskForActor(taskService, actorIdTranslator);
		processInstance = checkProcessIsStillActive(ksession, processInstance);

		String actorIdReviewer = "reviewer";
		completeTaskForActor(taskService, actorIdReviewer);

		// check the state of process instance
		// The process shall be terminated
		checkProcessIsTerminated(ksession, processInstance);
	}
	
	@Test
	public void extendedHumaTaskTest() {
		String bpmnProcessId = "org.mahu.proto.jbpm6.extendedHumanTask";
	}


	private void checkProcessIsTerminated(KieSession ksession,
			org.kie.api.runtime.process.ProcessInstance processInstance) {
		processInstance = ksession.getProcessInstance(processInstance.getId());
		assertNull(processInstance);
	}

	private org.kie.api.runtime.process.ProcessInstance checkProcessIsStillActive(
			KieSession ksession,
			org.kie.api.runtime.process.ProcessInstance processInstance) {
		processInstance = ksession.getProcessInstance(processInstance.getId());
		assertNotNull(processInstance);
		assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
		return processInstance;
	}

	private void completeTaskForActor(TaskService taskService,
			String actorIdReviewer) {
		List<TaskSummary> tasks;
		long taskId;
		tasks = taskService.getTasksAssignedAsPotentialOwner(actorIdReviewer,
				"en-UK");
		assertNotNull(tasks);
		assertEquals(1, tasks.size());

		taskId = tasks.get(0).getId();

		taskService.start(taskId, actorIdReviewer);
		taskService.complete(taskId, actorIdReviewer, null);
	}

	/*
	 * helper methods
	 */
	protected void cleanupSingletonSessionId() {
		File tempDir = new File(System.getProperty("java.io.tmpdir"));
		if (tempDir.exists()) {
			String[] jbpmSerFiles = tempDir.list(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.endsWith("-jbpmSessionId.ser");
				}
			});
			for (String file : jbpmSerFiles) {

				new File(tempDir, file).delete();
			}
		}
	}

}
