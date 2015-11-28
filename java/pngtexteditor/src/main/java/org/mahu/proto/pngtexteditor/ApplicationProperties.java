package org.mahu.proto.pngtexteditor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class ApplicationProperties {

	public static File lastVisitedDirectory;
	private final static String LAST_VISITED_DIR = "LAST_VISITED_DIR";

	private final static String VERSION = "0.1";
	private final static String CONFIG_FILE = "config.properties";

	public static String getUserDataDirectory() {
		String dir = System.getProperty("user.home") + File.separator
				+ ".pngtexteditor" + File.separator
				+ getApplicationVersionString() + File.separator;
		File d = new File(dir);
		if (!d.exists()) {
			d.mkdirs();
		}
		return dir;
	}

	public static String getApplicationVersionString() {
		return VERSION;
	}

	public static void saveProperties() {
		Properties prop = new Properties();
		OutputStream output = null;

		try {
			output = new FileOutputStream(getUserDataDirectory() + CONFIG_FILE);

			if (lastVisitedDirectory != null) {
				prop.setProperty(LAST_VISITED_DIR, lastVisitedDirectory.getAbsolutePath());
			}
			String comments = null;
			prop.store(output, comments);

		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}

	public static void readProperties() {
		Properties prop = new Properties();
		InputStream input = null;

		try {
			File file = new File(getUserDataDirectory() + CONFIG_FILE);
			if (file.exists()) {
				input = new FileInputStream(getUserDataDirectory()
						+ CONFIG_FILE);
				prop.load(input);
				if (prop.getProperty(LAST_VISITED_DIR) != null) {
					lastVisitedDirectory = new File(
							prop.getProperty(LAST_VISITED_DIR));
					if (!lastVisitedDirectory.exists()) {
						lastVisitedDirectory = null;
					}
				}
			}

		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}
}
