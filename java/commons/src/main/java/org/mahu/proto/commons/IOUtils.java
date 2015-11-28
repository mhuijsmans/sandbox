package org.mahu.proto.commons;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class IOUtils {

	/**
	 * Return the URL of the resource
	 * 
	 * @param cls
	 *            , class (main or test) used to resolve the requested resource
	 * @param resourceName
	 *            , name of resource to resolve. Format can be path/name or
	 *            name.
	 * @return URL
	 */
	public static URL getResourceUrl(final Class<?> cls,
			final String resourceName) {
		return cls.getClassLoader().getResource(resourceName);
	}

	/**
	 * Return File representation of resource. Using the File (e.g. reading)
	 * will only work if the URL represent a file on the file system and not a
	 * file inside a jar.
	 * 
	 * @param cls
	 *            , class (main or test) used to resolve the requested resource
	 * @param resourceName
	 *            , name of resource to resolve. Format can be path/name or
	 *            name.
	 * @return URL
	 */
	public static File getResourceFile(final Class<?> cls,
			final String resourceName) {
		URL url = getResourceUrl(cls, resourceName);
		File f = new File(url.getFile());
		return f;
	}
	
	public static byte[] readAllBytes(final File dir, final String filename)
			throws IOException {
		final File image = new File(dir, filename);
		return readAllBytes(image);
	}

	public static byte[] readAllBytes(final File file) throws IOException {
		Path path = Paths.get(file.toURI());
		return Files.readAllBytes(path);
	}
	
	public static void writeBytes(final File file, final byte[] b) throws IOException {
		File dir = file.getParentFile();
		if (!dir.exists()) {
			dir.mkdirs();
		}
		Path path = Paths.get(file.toURI());
		Files.write(path, b);
	}	
	
	public static void deleteFile(final File file) throws IOException {
		if (file.exists()) {
			file.delete();
			if (file.exists()) {
				throw new IOException("Failed to delete file "+file.getAbsolutePath());
			}
		}
	}	

}
