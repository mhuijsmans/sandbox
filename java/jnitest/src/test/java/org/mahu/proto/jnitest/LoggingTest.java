package org.mahu.proto.jnitest;

import static org.junit.Assert.assertEquals;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.mahu.proto.commons.Chrono;
import org.mahu.proto.jnitest.nativewrapper.HelloJNI;
import org.mahu.proto.jnitest.nativewrapper.LoggingInterface;
import org.mahu.proto.jnitest.nativewrapper.NarSystem;

public class LoggingTest {

	private static Logger LOGGER = LogManager.getLogger(LoggingTest.class);
	
	static int JNI_LOGTEST_BASIC = 0;
	static int JNI_LOGTEST_100000_times_a = 1;
	static int JNI_LOGTEST_100000_times_a_optimised = 2;

	@Before
	public void init() {
		NarSystem.loadLibrary();
	}

	@Test
	public void testLogging() {
		LOGGER.info("Start of log test");
		// preparation
		HelloJNI app = new HelloJNI();
		MyLogging log = new MyLogging();
		// test
		int result = app.logging(log,JNI_LOGTEST_BASIC);
		assertEquals(0, result);
		LOGGER.info("End of log test");
	}
	
	@Test
	public void testLogManyMessages() {
		// preparation
		int max = 100000;
		HelloJNI app = new HelloJNI();
		MyBufferedLogger log = new MyBufferedLogger();
		// test
		Chrono chrono = new Chrono();
		int result = app.logging(log,JNI_LOGTEST_100000_times_a);
		LOGGER.info("result "+chrono.elapsedAndAvg(max));
		assertEquals(0, result);
		assertEquals(max, log.sb.length());
		String a = "aaaaaaaaaaaaaaaa";
		assertEquals(a, log.sb.toString().substring(0,  a.length()));
	}
	
	@Test
	public void testLogManyMessagesOptimzed() {
		// preparation
		int max = 100000;
		HelloJNI app = new HelloJNI();
		MyBufferedLogger log = new MyBufferedLogger();
		// test
		Chrono chrono = new Chrono();
		int result = app.logging(log,JNI_LOGTEST_100000_times_a_optimised);
		LOGGER.info("result optimised logger "+chrono.elapsedAndAvg(max));
		assertEquals(0, result);
		assertEquals(max, log.sb.length());
		String a = "aaaaaaaaaaaaaaaa";
		assertEquals(a, log.sb.toString().substring(0,  a.length()));
	}	

	class MyLogging implements LoggingInterface {

		public void functional(String logInfo) {
			System.out.println(logInfo);
			LOGGER.info("FUNC: " + logInfo);
		}

		public void trace(String logInfo) {
			System.out.println(logInfo);
			LOGGER.warn("TRACE: " + logInfo);
		}

	}
	
	class MyBufferedLogger implements LoggingInterface {
		
		private StringBuffer sb = new StringBuffer(); 

		public void functional(String logInfo) {
			sb.append(logInfo);
		}

		public void trace(String logInfo) {
			sb.append(logInfo);
		}

	}	

}
