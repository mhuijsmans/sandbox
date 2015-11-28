package org.mahu.proto.systemtest.logging;

import java.io.File;

public class Logging {
	
	public static void setLoggingProperties() {
		System.setProperty("java.util.logging.config.file","target/classes/logging.properties");
		// This must be the first statement, so logging is in place before it is used.
		Logging.assertThatIfLoggingPropertyIsSetThatItExists();
	}

	/**
	 * Java.util.logging will be silently if the property file doesn't exist. 
	 * It will simply not generate logging output. This method performs the check. 
	 */
	public static void assertThatIfLoggingPropertyIsSetThatItExists() {
		final String filePath = System.getProperty("java.util.logging.config.file");
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
