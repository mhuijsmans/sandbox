package org.mahu.proto.proxytest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ProxyTest {

	/**
	 * Test: call MyTestInterface1 via a generic proxy
	 */
	@Test
	public void callTestInterface1ViaProxy() {
		// Preparation
		String testName = "test2";
		TestInterface1 testInterface = ProxyCreator.createTestInterfaceProxy(new MyTestInterface1());
		TestInterface1.RequestData requestData = new TestInterface1.RequestData();
		requestData.setName(testName);
		// test
		TestInterface1.ResponseData  response = testInterface.process(requestData);
		assertTrue(response!=null);
		assertEquals(testName, response.getName());
	}

	/**
	 * Test: call MyTestInterface2 via a generic proxy
	 */	
	@Test
	public void callTestInterface2ViaProxy() {
		// Preparation
		String testName = "test2";
		TestInterface2 testInterface = ProxyCreator.createTestInterface2Proxy(new MyTestInterface2());
		TestInterface2.RequestData requestData = new TestInterface2.RequestData();
		requestData.setName(testName);
		// test
		TestInterface2.ResponseData  response = testInterface.process(requestData);
		assertTrue(response!=null);
		assertEquals(testName, response.getName());
	}

}
