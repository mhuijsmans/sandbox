package org.mahu.proto.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.mahu.proto.logging.helper.Helper;

public class Log4j2RootLoggerTest {

	static {
		Log4j2Util.setConfigFile("log4j2RootLogger.xml");
	}

	@Test
	public void logViaRootLoggerWhichInNotInXml() {
		Logger log = LogManager.getLogger(Log4j2RootLoggerTest.class);
		log.error("Error");
		log.info("Hi, I am not printed #########################################");
		log.debug("Hi again, I am not printed #########################################");
	}

	@Test
	public void logForHelperWithOwnLoggerAndAppenderUsingClassName() {
		Logger log = LogManager.getLogger(Helper.class);
		log.info("Helper Hi");
		log.debug("Helper Hi again");
		log.error("Helper Error");
	}

}
