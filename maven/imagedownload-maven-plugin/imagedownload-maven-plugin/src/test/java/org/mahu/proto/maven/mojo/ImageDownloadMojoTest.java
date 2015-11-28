package org.mahu.proto.maven.mojo;

import java.io.File;
import java.net.URL;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

public class ImageDownloadMojoTest extends AbstractMojoTestCase {

	private final static File testProjectBaseDir = getResourceFile(
			ImageDownloadMojoTest.class, "unit/project-to-test");
	private final static File testProjectTargetDir = new File(
			testProjectBaseDir, "target");
	private final static File imageDir = new File(testProjectTargetDir,
			"test-classes/images");
	private final File image1 = new File(imageDir, "image1.png");
	private final File image2 = new File(imageDir, "image2.png");

	public void testMojo() throws Exception {
		JettyEmbeddedRunner server = new JettyEmbeddedRunner();
		try {
			server.startServer();

			deleteFile(image1);
			deleteFile(image2);

			executeImageDownloadMojo();

			assertTrue(image1.exists());
			assertTrue(image2.exists());

			// If images already exist, they do not need to be downloaded
			executeImageDownloadMojo();

			assertTrue(image1.exists());
			assertTrue(image2.exists());

			server.stop();
			server.await();
			server = null;

			// If images already exist, it doesn't matter if server is running
			// or not.
			executeImageDownloadMojo();

			assertTrue(image1.exists());
			assertTrue(image2.exists());
		} finally {
			if (server != null) {
				server.stop();
				server.await();
			}
		}
	}

	public void testMojoNoServerNoImagesDownloadedYet() throws Exception {
		try {
			deleteFile(image1);
			deleteFile(image2);

			executeImageDownloadMojo();
			fail();
		} catch (MojoExecutionException e) {
			assertTrue(true);
		}
	}

	protected void executeImageDownloadMojo() throws Exception,
			MojoExecutionException {
		// preparation
		// the test pom.xml also contains the name of the mojo artifactId
		File pom = new File(testProjectBaseDir, "pom.xml");
		ImageDownloadMojo myMojo = (ImageDownloadMojo) lookupMojo(
				"imagedownload", pom);
		myMojo.execute();
	}

	protected void deleteFile(final File image) {
		image.delete();
		assertFalse(image.exists());
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