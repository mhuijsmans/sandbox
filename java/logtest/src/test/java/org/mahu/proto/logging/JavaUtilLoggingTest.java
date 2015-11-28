package org.mahu.proto.logging;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mahu.proto.commons.IOUtils;

public class JavaUtilLoggingTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Test
	public void testNotExistingPropertyFile() throws SecurityException,
			IOException {
		// Preparation
		File loggingPropertiesFile = IOUtils.getResourceFile(
				JavaLoggingApp.class, JavaLoggingApp.LOGGING_PROPERTIES2);
		LogManager logManager = setLoggingPropertyFile(loggingPropertiesFile
				.getAbsolutePath() + "_unknown");
		// test
		exception.expect(FileNotFoundException.class);
		logManager.readConfiguration();
	}

	@Test
	public void testInvalidReadPropertyFile() throws SecurityException,
			IOException {
		LogManager logManager = setLoggingPropertyFileFromResource("logging_invalid.properties");
		// test
		logManager.readConfiguration();
		fail("Expected exception doesn't occur");
	}

	@Test
	public void testReadPropertyFile() throws SecurityException, IOException, InterruptedException {
		LogManager logManager = setLoggingPropertyFileFromResource(JavaLoggingApp.LOGGING_PROPERTIES2);
		logManager.readConfiguration();
		assertTrue(true);
		Logger LOGGER = Logger.getLogger(JavaUtilLoggingTest.class.getName());
		LOGGER.info("log message");
		// Log info can be flushed
//		for(Handler h: LOGGER.getHandlers()) {
//			h.flush();
//		}
		assertTrue(MyFormatter1.logCntr>0);
	}

	protected LogManager setLoggingPropertyFileFromResource(
			final String loggingPropertiesFileName) {
		File loggingPropertiesFile = IOUtils.getResourceFile(
				JavaLoggingApp.class, loggingPropertiesFileName);
		return setLoggingPropertyFile(loggingPropertiesFile.getAbsolutePath());
	}

	protected LogManager setLoggingPropertyFile(File loggingPropertiesFile) {
		return setLoggingPropertyFile(loggingPropertiesFile.getAbsolutePath());
	}

	protected LogManager setLoggingPropertyFile(
			final String loggingPropertiesFilePath) {
		System.out.println("propFile: " + loggingPropertiesFilePath);
		System.setProperty("java.util.logging.config.file",
				loggingPropertiesFilePath);
		LogManager logManager = LogManager.getLogManager();
		return logManager;
	}
}
