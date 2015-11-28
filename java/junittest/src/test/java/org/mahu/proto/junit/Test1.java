package org.mahu.proto.junit;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class Test1 {
	@Before
	public void beforeEachTest() {
	}

	@After
	public void afterEachTest() {
	}

	@BeforeClass
	public static void testSetup() {
	}

	@AfterClass
	public static void testCleanup() {
		// Teardown for data used by the unit tests
	}

	@Test
	public void testApp() {
		assertTrue(true);
	}

	// http://junit.org/javadoc/4.11/org/junit/Test.html
	@Test (expected = Exception.class)
	public void testingException() throws Exception {
		throwException();
		fail("must never reach this point");
	}
	
	// http://junit.org/javadoc/4.11/org/junit/Test.html
	@Test (timeout=100)
	public void testingTimeout()  {
		// should fail
		sleep1sec();
	}
	
	// See http://junit.org/javadoc/4.11/
	@Ignore("not ready yet") @Test 
	public void notReadyYet() {
		fail("not ready yet");
	}

	private void throwException() throws Exception {
		throw new Exception();
	}
	
	static void sleep1sec() {
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			// ignore
		}
	}	
}
