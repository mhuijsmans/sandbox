package org.mahu.proto.logging;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;

class ConfigureLogging {
	private static boolean isLoaded = false;

	static void LoadConfiguration() {
		if (isLoaded == false) {
			try {
				InputStream is = ConfigureLogging.class.getClassLoader()
						.getResourceAsStream("logging.test.properties");
				LogManager.getLogManager().readConfiguration(is);
				System.out.println("Log configuration is installed");
			} catch (SecurityException | IOException e) {
				System.out
						.println("Log configuration installation failed, exception="
								+ e.getMessage());
			} finally {
				isLoaded = true;
			}
		}
	}
}
