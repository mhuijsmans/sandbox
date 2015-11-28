package org.mahu.proto.proxytest;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.logging.Logger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mahu.proto.commons.IOUtils;
import org.mahu.proto.rmitest.RmiRegistryWrapper;
import org.mahu.proto.rmitest.TaskExecutor;
import org.mahu.proto.rmitest.TaskExecutorImpl;

public class TestRmi {
	private static final Logger log = Logger.getLogger(TestRmi.class.getName());

	private static RmiRegistryWrapper rmiRegistry;
	private static TaskExecutorImpl ce;
	private static TaskExecutor taskExecutor;

	@BeforeClass
	public static void init() throws RemoteException, NotBoundException {
		// Security policies need to be set.
		// Note that the solution in test (allow everything) is not suited for
		// production.
		File policyFile = IOUtils.getResourceFile(TestRmi.class, "java.policy");
		System.setProperty("java.security.policy", policyFile.getAbsolutePath());
		// An RMI solution requires a RMI Registry service
		rmiRegistry = new RmiRegistryWrapper();
		rmiRegistry.startRmiRegistery();
		// Publish the Compute service in the registry so that it can be invoked
		ce = new TaskExecutorImpl();
		ce.bindTaskExecutorService();
		//
		taskExecutor = ce.lookupTaskExecutorServiceInRegistry();
	}

	@AfterClass
	public static void exit() throws RemoteException {
		// Remove service from registry
		ce.unBindTaskExecutorService();
		// Stopping RmiRegistry is not possible.
	}

	@Test(timeout = 1000)
	public void testIntegerTask() throws RemoteException, NotBoundException {
		// Preparation
		MyIntTask task = new MyIntTask();
		// test
		Integer result = taskExecutor.executeTask(task);
		assertTrue(result != null);
		assertTrue(result.intValue() == task.getValue());
	}

	@Test(timeout = 1000)
	public void testCustomObjectTask() throws RemoteException,
			NotBoundException {
		// Preparation
		MyRequestTask task = new MyRequestTask();
		// test
		MyRequestTask.MyResponse result = taskExecutor.executeTask(task);
		assertTrue(result != null);
		assertTrue(result.getValue() == task.getValue());
	}

	/*
	 * This must be a static class, because otherwise RMI also want to serialize
	 * TestRmi. It return an Integer
	 */
	static class MyIntTask implements TaskExecutor.Task<Integer>, Serializable {

		private static final long serialVersionUID = 4870354658317834077L;
		private final int value = 100;

		@Override
		public Integer execute() {
			log.info("hi");
			return new Integer(value);
		}

		public int getValue() {
			return value;
		}

	}

	/**
	 * This task returns a own defined class as response.
	 */
	static class MyRequestTask implements
			TaskExecutor.Task<MyRequestTask.MyResponse>, Serializable {

		private static final long serialVersionUID = 4870354658317834077L;
		private final int value = 100;

		static class MyResponse implements Serializable {

			private static final long serialVersionUID = -312059525581427848L;

			private int value;

			int getValue() {
				return value;
			}

			void setValue(int value) {
				this.value = value;
			}

		}

		@Override
		public MyResponse execute() {
			log.info("hi");
			MyResponse response = new MyRequestTask.MyResponse();
			response.setValue(value);
			return response;
		}

		public int getValue() {
			return value;
		}

	}

}
