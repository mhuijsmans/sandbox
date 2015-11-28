package org.mahu.proto.logging.other;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnotherLogbackClass {
	
	private static Logger LOGGER = (Logger) LoggerFactory
			.getLogger(AnotherLogbackClass.class);
	
	public void doNothingInfo() {
		LOGGER.info("I wish life is always so info");
	}
	
	public void doNothingDebug() {
		LOGGER.debug("I wish life is always so debug");
	}
	

}
