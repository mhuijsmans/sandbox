package org.mahu.proto.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mahu.proto.logging.other.AnotherLog4j2Class;

public final class Log4j2App {
	
	private static Logger LOGGER = LogManager.getLogger(Log4j2App.class);

	public static void main(String[] args) {
		loggingExamples();
	}
	
	private static void loggingExamples() {
		System.out
				.println("==================================================================");
		// Examples
		LOGGER.fatal("fatal");
		LOGGER.error("eror");
		LOGGER.warn("warning");
		LOGGER.info("info");
		LOGGER.debug("debug");
		LOGGER.trace("trace");
		// example general (not exhaustive)  
//		LOGGER.log(Level.INFO,"info.using log");
//		LOGGER.log(Level.DEBUG, "some message", new SomeException());
		//
		// Calling another class
		AnotherLog4j2Class ac = new AnotherLog4j2Class();
		ac.doNothingInfo();
		ac.doNothingDebug();
	}	
	
	static class SomeException extends Throwable {
		private static final long serialVersionUID = 1L;
	}

}
