package org.mahu.proto.logging;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mahu.proto.logging.other.AnotherLoggerClass;

/**
 * This class explores Java Logging
 * 
 * Used sources:
 * 
 * http://tutorials.jenkov.com/java-logging/logger.html
 * http://tutorials.jenkov.com/java-logging/logger-hierarchy.html
 * http://tutorials.jenkov.com/java-logging/levels.html
 * http://tutorials.jenkov.com/java-logging/formatters.html
 * http://tutorials.jenkov.com/java-logging/filters.html
 * http://tutorials.jenkov.com/java-logging/handlers.html
 * http://tutorials.jenkov.com/java-logging/logrecord.html
 * http://tutorials.jenkov.com/java-logging/configuration.html
 * http://tutorials.jenkov.com/java-logging/logmanager.html
 * 
 * The above links also include diagrams, e.g. on how hierarchy and
 * filter/logger/handler structure.
 * 
 * An example of a formatter that takes as input a LogRecord and generates a
 * string: http://www.vogella.com/tutorials/Logging/article.html
 */
public class JavaLoggingApp {

	public final static String LOGGING_PROPERTIES2 = "logging2.properties";
	private final static Logger LOGGER = Logger.getLogger(JavaLoggingApp.class
			.getName());

	public static void main(String[] args) throws IOException {
		assertThatIfLoggingPropertyIsSetThatItExists();
		Thread.currentThread().setName("mainthread");
		loggingExamples();
	}

	private static void loggingExamples() {
		System.out
				.println("==================================================================");
		// Examples
		LOGGER.severe("severe");
		LOGGER.warning("warning");
		LOGGER.info("info");
		LOGGER.config("config");
		LOGGER.fine("fine");
		LOGGER.finer("finer");
		LOGGER.finest("finest");
		// example general (not exhaustive)
		LOGGER.log(Level.INFO, "This is a {0} string with {1} text.",
				new Object[] { "formatted", "inserted" });
		LOGGER.log(Level.SEVERE, "Exception while ", new Exception());
		//
		// Calling another class
		AnotherLoggerClass ac = new AnotherLoggerClass();
		ac.doNothingInfo();
		ac.doNothingFine();
	}

	private static void assertThatIfLoggingPropertyIsSetThatItExists() {
		String filePath = System.getProperty("java.util.logging.config.file");
		if (filePath != null) {
			File file = new File(filePath);
			if (!file.exists()) {
				throw new AssertionError(
						"Java Util property file doesn't exist: "
								+ file.getAbsolutePath());
			}
		}
	}

}
