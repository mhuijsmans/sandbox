package org.mahu.proto.xsdtest;

import java.io.File;
import java.net.URL;

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

}
