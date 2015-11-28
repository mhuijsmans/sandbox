package org.mahu.proto.proxytest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mahu.proto.commons.Chrono;
import org.mahu.proto.commons.IOUtils;
import org.mahu.proto.proxytest.TestInterface1.RequestData;
import org.mahu.proto.proxytest.TestInterface1.ResponseData;
import org.mahu.proto.proxytest.remote.RequestExecutorImpl;
import org.mahu.proto.proxytest.remote.RmiRegistryWrapper;

public class RemoteProxyTest {
	
	private static final Logger log = Logger
			.getLogger(RemoteProxyTest.class.getName());

	private static RmiRegistryWrapper rmiRegistry;
	private RemoteService remoteService;

	@BeforeClass
	public static void init() throws RemoteException, NotBoundException {
		// Security policies need to be set.
		// Note that the solution in test (allow everything) is not suited for
		// production.
		File policyFile = IOUtils.getResourceFile(RemoteProxyTest.class,
				"java.policy");
		System.setProperty("java.security.policy", policyFile.getAbsolutePath());
		// An RMI solution requires a RMI Registry service
		rmiRegistry = new RmiRegistryWrapper();
		rmiRegistry.startRmiRegistery();
	}

	@AfterClass
	public static void exit() throws RemoteException {
		// Remove service from registry
		// Stopping RmiRegistry is not possible.
	}

	@Before
	public void beforeTest() {

	}

	@After
	public void afterTest() throws RemoteException {
		if (remoteService != null) {
			remoteService.stop();
		}
	}

	/**
	 * Test: call MyTestInterface1 via a generic proxy
	 * 
	 * @throws RemoteException
	 * @throws NotBoundException
	 */
	@Test
	public void callTestInterface1ViaProxy() throws RemoteException,
			NotBoundException {
		// Preparation
		String name = "remote";
		// Create the remote service
		// Register the service implementations
		RemoteService remoteService = new RemoteService(
				new Class<?>[] { MyTestInterface1.class });
		remoteService.start();
		//
		ProxyCreator
				.setRequestExecutor(remoteService.getLocalRequestExecutor());
		//
		TestInterface1 testInterface1 = ProxyCreator
				.createTestInterface1RemoteProxy();
		// Test
		RequestData requestData = new RequestData();
		requestData.setName(name);
		ResponseData response = testInterface1.process(requestData);
		assertTrue(response != null);
		assertEquals(name, response.getName());
	}

	@Test
	public void callDifferentInterfacesViaProxy() throws RemoteException,
			NotBoundException {
		// Preparation
		String name = "remote";
		// Create the remote service
		// Register the service implementations
		RemoteService remoteService = new RemoteService(new Class<?>[] {
				MyTestInterface1.class, MyTestInterface2.class });
		remoteService.start();
		//
		ProxyCreator
				.setRequestExecutor(remoteService.getLocalRequestExecutor());
		// Test (1)
		TestInterface1 testInterface1 = ProxyCreator
				.createTestInterface1RemoteProxy(TestInterface1.class);
		RequestData requestData1 = new RequestData();
		requestData1.setName(name);
		ResponseData response1 = testInterface1.process(requestData1);
		assertTrue(response1 != null);
		assertEquals(name, response1.getName());
		//
		// Test (2)
		TestInterface2 testInterface2 = ProxyCreator
				.createTestInterface1RemoteProxy(TestInterface2.class);
		TestInterface2.RequestData requestData2 = new TestInterface2.RequestData();
		requestData2.setName(name);
		requestData2.setBytes(new byte[256]);
		TestInterface2.ResponseData response2 = testInterface2
				.process(requestData2);
		assertTrue(response2 != null);
		assertEquals(name, response2.getName());
	}

	@Test
	public void callBigDataViaProxy() throws RemoteException, NotBoundException {
		// Preparation
		String name = "remote";
		// Create the remote service
		// Register the service implementations
		RemoteService remoteService = new RemoteService(new Class<?>[] {
				MyTestInterface1.class, MyTestInterface2.class });
		remoteService.start();
		//
		ProxyCreator
				.setRequestExecutor(remoteService.getLocalRequestExecutor());
		// Test
		TestInterface2 testInterface2 = ProxyCreator
				.createTestInterface1RemoteProxy(TestInterface2.class);
		for (int i = 0; i <= 32; i++) {
			Chrono chrono = new Chrono();
			TestInterface2.RequestData requestData2 = new TestInterface2.RequestData();
			requestData2.setName(name);
			requestData2.setBytes(new byte[256 * 256 * i]);
			TestInterface2.ResponseData response2 = testInterface2
					.process(requestData2);
			assertTrue(response2 != null);
			assertEquals(name, response2.getName());
			log.info("Nr of bytes: "+requestData2.getBytes().length+" in ms: "+chrono.elapsedMs());
		}
	}

}
