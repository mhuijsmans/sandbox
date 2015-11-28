package org.mahu.proto.logging.other;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AnotherLog4j2Class {
	
	private static Logger LOGGER = LogManager.getLogger(AnotherLog4j2Class.class);
	
	public void doNothingInfo() {
		LOGGER.info("I wish life is always so info");
	}
	
	public void doNothingDebug() {
		LOGGER.debug("I wish life is always so debug");
	}
	

}
