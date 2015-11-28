package org.mahu.proto.logging.other;

import org.apache.log4j.Logger;

public class AnotherLog4jClass {
	
	static Logger LOGGER = Logger.getLogger(AnotherLog4jClass.class);
	
	public void doNothingInfo() {
		LOGGER.info("I wish life is always so info");
	}
	
	public void doNothingDebug() {
		LOGGER.debug("I wish life is always so debug");
	}
	

}
