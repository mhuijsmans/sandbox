package org.mahu.proto.maven.mojo.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;

import org.junit.Test;

public class AppTest {

	private final static File testClassesDir = getResourceFile(AppTest.class,
			".");
	private final File image1 = new File(testClassesDir, "images/no_image.png");

	@Test
	public void testThtaUsesDownloadedImages() {
		assertTrue(testClassesDir.exists());
		assertTrue("Image doesn't exists: "+image1.getAbsolutePath(), image1.exists());
	}

	private static File getResourceFile(final Class<?> cls,
			final String resourceName) {
		URL url = getResourceUrl(cls, resourceName);
		File f = new File(url.getFile());
		return f;
	}

	private static URL getResourceUrl(final Class<?> cls,
			final String resourceName) {
		return cls.getClassLoader().getResource(resourceName);
	}
}
