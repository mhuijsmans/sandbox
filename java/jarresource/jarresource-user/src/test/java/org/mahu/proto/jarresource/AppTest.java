package org.mahu.proto.jarresource;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.junit.Test;

public class AppTest {

	private static final String ENCODING_UTF8 = "UTF-8";

	/**
	 * Check that check that resources from different jars can be found.
	 * 
	 * @throws UnsupportedEncodingException
	 */
	@Test
	public void resourceIsresolved() throws UnsupportedEncodingException {
		getResourceAsFile("file1-1.txt");
		getResourceAsFile("data/file1-2.txt");
		System.out.println(JarResource1.class.getClassLoader()
				.getResource(JarResource1.class.getName().replace('.', '/') + ".class").toString());

		getResourceAsFile("file2-1.txt");
		getResourceAsFile("data/file2-2.txt");
		System.out.println(JarResource2.class.getClassLoader()
				.getResource(JarResource2.class.getName().replace('.', '/') + ".class").toString());
	}

	/**
	 * with getResourceAsFile it can be observed that (when run from commandline)
	 * that the file does not exists, can not be read and written
	 * 
	 * @throws IOException
	 */
	@Test
	public void resourceIsReadOnly() throws IOException {
		File f = getResourceAsFile("file1-1.txt");
		System.out.println("exists=" + f.exists());
		System.out.println("canRead=" + f.canRead());
		System.out.println("canWrite=" + f.canWrite());
	}

	/**
	 * With getResourceAsTempFile a resource from a jar can be made available as
	 * File. The file is a temporary file that is deleted on exit.
	 * 
	 * @throws IOException
	 */
	@Test
	public void resourceAsTempFile() throws IOException {
		File f = getResourceAsTempFile("file1-1.txt");
		System.out.println("exists=" + f.exists());
		System.out.println("canRead=" + f.canRead());
		System.out.println("canWrite=" + f.canWrite());

		byte[] fileData = Files.readAllBytes(f.toPath());
		assertEquals(3, fileData.length);
		assertEquals("123", new String(fileData, ENCODING_UTF8));
	}

	/**
	 * Find a resource of the specified name from the search path used to load
	 * classes. This method locates the resource through the system classloader (see
	 * ClassLoader.getSystemClassLoader()).
	 * 
	 * @param resourcePath
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static File getResourceAsFile(final String resourcePath) throws UnsupportedEncodingException {
		final URL inputDataFile = ClassLoader.getSystemResource(resourcePath);
		if (inputDataFile == null) {
			throw new RuntimeException("File not found " + resourcePath);
		}

		File f = new File(URLDecoder.decode(inputDataFile.getFile(), ENCODING_UTF8));

		System.out.println(inputDataFile.getFile().toString());
		System.out.println(inputDataFile.toString());
		// Next lines results in an exception when test is executed from commandline.
		// Reason is that the file is inside a jar and there is no Path format for that.
		// System.out.println(f.toPath().toString());
		// System.out.println(f.toPath().getFileName());

		return f;
	}

	/**
	 * Find a resource of the specified name from the search path used to load
	 * classes. This method locates the resource through the system classloader (see
	 * ClassLoader.getSystemClassLoader()).
	 * 
	 * This method is useful when reading a resource from a jar.
	 * 
	 * @param resourcePath
	 * @return File of the
	 * @throws UnsupportedEncodingException
	 */
	public static File getResourceAsTempFile(final String resourcePath) throws IOException {
		final InputStream in = ClassLoader.getSystemResourceAsStream(resourcePath);
		return stream2file(in);
	}

	public static File stream2file(final InputStream in) throws IOException {
		final File tempFile = File.createTempFile(UUID.randomUUID().toString(), ".tmp");
		System.out.println("temp file=" + tempFile.getAbsolutePath());
		tempFile.deleteOnExit();
		Files.copy(in, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		return tempFile;
	}
}
