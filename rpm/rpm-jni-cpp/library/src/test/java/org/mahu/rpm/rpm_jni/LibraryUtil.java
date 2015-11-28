package org.mahu.rpm.rpm_jni;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.security.CodeSource;
import java.util.logging.Logger;

/**
 * Library supports copying is dynamic libraries
 */

// INFO: library.path
// /home/martien/mahu_googlecode/trunk/rpm/rpm-jni-cpp/library/target/nar/library-0.1.0-SNAPSHOT-amd64-Linux-gpp-jni/lib/amd64-Linux-gpp/jni:/usr/java/packages/lib/amd64:/usr/lib64:/lib64:/lib:/usr/lib

public class LibraryUtil {

	private final static Logger LOGGER = Logger.getLogger(LibraryUtil.class
			.getName());

	private File destDir;

	public void copyLibrary(final String path, final String fileName) {
		File srcFile = new File(new File(path), fileName);
		if (!srcFile.exists()) {
			throw new RuntimeException("File does not exist: " + srcFile);
		}
		File destFile = new File(getPathInTarget(), fileName);
		if (destFile.exists()) {
			if (destFile.isDirectory()) {
				throw new RuntimeException("Destination file is a directory: "
						+ destFile);
			}
			if (!destFile.delete()) {
				throw new RuntimeException("Failed to delete file: " + destFile);
			}
		}
		// Always copy because stub and real library have same name
		try {
			Files.copy(srcFile.toPath(), destFile.toPath());
		} catch (IOException e) {
			throw new RuntimeException("Failed to copy " + srcFile + " to "
					+ destFile, e);
		}
	}

	private File getPathInTarget() {
		if (destDir == null) {
			// From traces I have concluded that targetPath is always first
			String path = getCurrentLibraryPath();
			int index = path.indexOf(':');
			if (index > 0) {
				path = path.substring(0, index);
			}
			if (!path.startsWith("/home/")
					|| path.indexOf("/library/target/nar/library") < 0) {
				throw new RuntimeException("Can not determine valid path: "
						+ path);
			}
			destDir = new File(path);
			if (!destDir.exists() || !destDir.isDirectory()) {
				throw new RuntimeException(
						"Dir does not exists, or is a file: " + destDir);
			}
		}
		return destDir;
	}

	private static String getCurrentLibraryPath() {
		return System.getProperty("java.library.path");
	}

	/**
	 * Check is test is integration test by looking at the library path. If that
	 * includes the target dir, it is.
	 * 
	 * Examples library.path:
	 * 
	 * In-jvm test: usr/java/packages/lib/amd64:/usr/lib64:/lib64:/lib:/usr/lib
	 * 
	 * Forked jvm test:
	 * /home/martien/mahu_googlecode/trunk/rpm/rpm-jni-cpp/library/target/nar
	 * /library
	 * -0.1.0-SNAPSHOT-amd64-Linux-gpp-jni/lib/amd64-Linux-gpp/jni:/usr/java
	 * /packages/lib/amd64:/usr/lib64:/lib64:/lib:/usr/lib
	 * 
	 * The library.path is set when a new process is forked. It is used by the
	 * forked process and it can not be changed in runtime. It is also used by
	 * JVM to load libraries. It can be changed in runtime. For dependent
	 * libraries, the process will only use the library.path set at startup,
	 * regardless of the library.path changes that may have been made.
	 * 
	 * @return true is the target directory is part of the test. 
	 */
	public static boolean isIntegrationTest() {
		// next line will (if you print it) return something like:
		// file:/home/martien/mahu_googlecode/trunk/rpm/rpm-jni-cpp/library/target/test-classes/
		URL url = LibraryUtil.class.getClassLoader().getResource(".");
		LOGGER.fine("url: " + url);
		// But I am interested in the target directory
		File dir = urlToFile(url).getParentFile();
		LOGGER.fine("Parent (target) dir: " + dir);
		String dirString = dir.getAbsolutePath();
		LOGGER.fine("dirString: " + dirString);
		return getCurrentLibraryPath().indexOf(dirString) >= 0;
	}

	public static void printWhereThisClassWasLoadedfrom(final Class<?> cls) {
		URL url = cls.getClassLoader().getResource(".");
		LOGGER.info("Class: " + cls.getName() + " was loaded from: " + url);
		CodeSource codeSource = cls.getProtectionDomain().getCodeSource();
		if (codeSource != null) {
			LOGGER.info("Class: " + cls.getName() + " codeSource "
					+ codeSource.getLocation());
		}
	}

	public static void printLibraryPath() {
		String libraryPath = System.getProperty("java.library.path");
		LOGGER.info("library.path " + libraryPath);
	}

	// Mtehod copied from:
	// https://weblogs.java.net/blog/kohsuke/archive/2007/04/how_to_convert.html
	private static File urlToFile(final URL url) {
		File f;
		try {
			f = new File(url.toURI());
		} catch (URISyntaxException e) {
			f = new File(url.getPath());
		}
		return f;
	}
}
