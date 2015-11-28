package org.mahu.proto.logging.other;

import java.util.logging.Logger;

public class AnotherLoggerClass {
	
	private final static Logger LOGGER = Logger.getLogger(AnotherLoggerClass.class
			.getName());
	
	public void doNothingInfo() {
		LOGGER.info("I wish life is always so info");
	}
	
	public void doNothingFine() {
		LOGGER.fine("I wish life is always so fine");
	}
	

}
