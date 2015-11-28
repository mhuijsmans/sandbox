package org.mahu.proto.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.mahu.proto.logging.helper.Helper;

public class Log4j2Test {

	static {
		Log4j2Util.setConfigFile("log4j2ToConsole.xml");
	}

	@Test
	public void logViaRootLogger() {
		Logger log = LogManager.getLogger(Log4j2Test.class);
		log.error("Error");
		// / next line is not printed, because of configuration rootLogger
		log.info("Hi, I am not printed #########################################");
		log.debug("Hi again, I am not printed #########################################");
	}

	@Test
	public void logForHelperWithOwnLoggerAndAppenderUsingClassName() {
		Logger log = LogManager.getLogger(Helper.class);
		log.info("Helper Hi");
		log.debug("Helper Hi again");
	}

	@Test
	public void logForAnotherHelperWithOwnLoggerUsingClassName() {
		Logger log = LogManager
				.getLogger(org.mahu.proto.logging.anotherhelper.Helper.class);
		log.info("AnotherHelper Hi");
		log.debug("AnotherHelper Hi again. This not not printed to [1]");
	}

	@Test
	public void logForHelperWithOwnLoggerAndAppenderUsingConfiguredName() {
		Logger log = LogManager.getLogger("DedicatedLogger");
		log.info("Dedicated Hi");
		log.debug("Dedicated Hi again.");
	}

}
