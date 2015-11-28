package org.mahu.proto.logging;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.mahu.proto.logging.other.AnotherLog4jClass;

public final class Log4jApp {
	
	static Logger LOGGER = Logger.getLogger(Log4jApp.class);

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
		LOGGER.log(Level.INFO,"info.using log");
		LOGGER.log(Level.DEBUG, "some message", new SomeException());
		//
		// Calling another class
		AnotherLog4jClass ac = new AnotherLog4jClass();
		ac.doNothingInfo();
		ac.doNothingDebug();
	}	
	
	static class SomeException extends Throwable {
		private static final long serialVersionUID = 1L;
	}

}
