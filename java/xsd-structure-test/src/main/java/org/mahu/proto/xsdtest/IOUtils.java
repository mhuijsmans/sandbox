package org.mahu.proto.xsdtest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class IOUtils {

	public static byte[] readAllBytes(final File file) throws IOException {
		Path path = Paths.get(file.toURI());
		return Files.readAllBytes(path);
	}

	public static File getDirectoryFromResource(final Class<?> cls,
			final String dirName) {
		return getResource(cls, dirName);
	}

	public static File getFileFromResource(final Class<?> cls,
			final String fileName) {
		return getResource(cls, fileName);
	}

	public static URL getResourceUrl(final Class<?> cls, final String dirName) {
		return cls.getClassLoader().getResource(dirName);
	}

	public static InputStream getResourceAsStream(final Class<?> cls,
			final String dirName) {
		return cls.getClassLoader().getResourceAsStream(dirName);
	}

	public static void writeStringToFile(final String text, final File file)
			throws IOException {
		final OutputStream os = new FileOutputStream(file);
		final PrintStream printStream = new PrintStream(os);
		printStream.print(text);
		printStream.close();
		os.close();
	}

	public static byte[] readAllBytes(final File dir, final String filename)
			throws IOException {
		final File image = new File(dir, filename);
		return IOUtils.readAllBytes(image);
	}

	private static File getResource(final Class<?> cls, final String dirName) {
		URL url = cls.getClassLoader().getResource(dirName);
		File f = new File(url.getFile());
		return f;
	}

	public static void writeStringToFile(final File outputFile,
			final String string) throws FileNotFoundException, IOException {
		OutputStream os = null;
		try {
			os = new FileOutputStream(outputFile);
			final PrintStream printStream = new PrintStream(os);
			printStream.print(string);
			printStream.close();
		} finally {
			if (os != null) {
				os.close();
			}
		}
	}

}
