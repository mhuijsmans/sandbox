package org.mahu.proto.jnitest;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;

/**
 * Library supports copying is dynamic libraries
 */

public class LibraryUtil {

	private File destDir;

	public void copyLibraryToTarget(final String path, final String fileName) {
		copyLibraryToTarget(new File(path), fileName);
	}

	public void copyLibraryToTarget(final File path, final String fileName) {
		File srcFile = new File(path, fileName);
		checkFileExists(srcFile);
		File libDir = getPathTargetLibraryDirectory();
		createLibraryDirectory(libDir);
		cleanLibraryDirectory(libDir);
		File destFile = new File(libDir, fileName);
		copyDynamicLibraryToLibraryDirectory(srcFile, destFile);
	}

	public File getMavenBasedir() {
		return getMavenTargetdir().getParentFile();
	}

	public File getMavenTargetdir() {
		URL url = LibraryUtil.class.getClassLoader().getResource(".");
		File targetDir = urlToFile(url).getParentFile();
		if (!targetDir.exists() || !targetDir.isDirectory()) {
			throw new RuntimeException("Found targetDir is not target: "
					+ targetDir.getAbsolutePath());
		}
		if (!targetDir.getAbsolutePath().endsWith("/target")) {
			throw new RuntimeException("TargetDir is not target: "
					+ targetDir.getAbsolutePath());
		}
		return targetDir;
	}

	public static void printLibraryPath() {
		System.out.println("library.path " + getCurrentLibraryPath());
	}

	protected void checkFileExists(File srcFile) {
		if (!srcFile.exists()) {
			throw new RuntimeException("File does not exist: " + srcFile);
		}
	}

	protected void copyDynamicLibraryToLibraryDirectory(File srcFile,
			File destFile) {
		try {
			Files.copy(srcFile.toPath(), destFile.toPath());
		} catch (IOException e) {
			throw new RuntimeException("Failed to copy " + srcFile + " to "
					+ destFile, e);
		}
	}

	protected void cleanLibraryDirectory(File libDir) {
		if (libDir.exists()) {
			if (libDir.isFile()) {
				throw new RuntimeException("Target lib directory is a file: "
						+ libDir);
			} else {
				deletAllFilesInDirectory(libDir);
			}
		}
	}

	protected void createLibraryDirectory(File libDir) {
		if (!libDir.exists()) {
			libDir.mkdirs();
			if (!libDir.exists()) {
				throw new RuntimeException(
						"Failed to create dyn-lib directory: " + libDir);
			}
		}
	}

	protected void deletAllFilesInDirectory(File libDir) {
		File[] childeren = libDir.listFiles();
		for (File file : childeren) {
			if (file.isFile()) {
				deleteFileAndCheckItIsDeleted(file);
			}
		}
	}

	protected void deleteFileAndCheckItIsDeleted(File file) {
		if (file.exists()) {
			if (file.isDirectory()) {
				throw new RuntimeException(
						"Destination file (to be deleted) is a directory: "
								+ file);
			}
			System.out.println("Deleting file: " + file);
			if (!file.delete()) {
				throw new RuntimeException("Failed to delete file: " + file);
			}
		}
	}

	private File getPathTargetLibraryDirectory() {
		if (destDir == null) {
			// In pom, the first pathElement is {project.build.directory}/lib
			String path = getCurrentLibraryPath();
			int index = path.indexOf(':');
			if (index > 0) {
				path = path.substring(0, index);
			}
			if (!path.endsWith("/target/lib")) {
				throw new RuntimeException(
						"Can not determine valid path (must end with /target/lib) : "
								+ path);
			}
			destDir = new File(path);
			if (destDir.exists() && !destDir.isDirectory()) {
				throw new RuntimeException("Dir exists and is a file: "
						+ destDir);
			}
		}
		return destDir;
	}

	private static String getCurrentLibraryPath() {
		return System.getProperty("java.library.path");
	}

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
