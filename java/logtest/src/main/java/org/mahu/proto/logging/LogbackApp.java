package org.mahu.proto.logging;

import org.mahu.proto.logging.other.AnotherLogbackClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogbackApp {

	private static Logger LOGGER = (Logger) LoggerFactory
			.getLogger(LogbackApp.class);

	public static void main(String[] args) {
		loggingExamples();
	}

	private static void loggingExamples() {
		System.out
				.println("==================================================================");
		// Examples, logback support formatting
		LOGGER.error("error {}", "value");
		LOGGER.warn("warning");
		LOGGER.info("info");
		LOGGER.debug("debug");
		LOGGER.trace("trace");
		//
		// Calling another class
		AnotherLogbackClass ac = new AnotherLogbackClass();
		ac.doNothingInfo();
		ac.doNothingDebug();
	}

	static class SomeException extends Throwable {
		private static final long serialVersionUID = 1L;
	}

}
