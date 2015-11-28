package org.mahu.proto.logging;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.mahu.proto.commons.IOUtils;

public class Log4j2Util {

	// http://stackoverflow.com/questions/21083834/load-log4j2-configuration-file-programmatically
	static void setConfigFile(final String configFileName) {
		File configFile = IOUtils.getResourceFile(Log4j2Util.class,
				configFileName);
		assertTrue(configFile.exists());
		System.setProperty("log4j.configurationFile", configFileName);
	}

}
