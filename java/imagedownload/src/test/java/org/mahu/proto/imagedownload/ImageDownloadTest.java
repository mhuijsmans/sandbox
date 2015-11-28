package org.mahu.proto.imagedownload;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ImageDownloadTest {

	private JettyEmbeddedRunner server = null;
	private final static String HOST = "localhost";
	private final static int PORT = 8976;

	@Before
	public void init() {
		server = new JettyEmbeddedRunner();
		server.startServer(HOST, PORT);
	}

	@After
	public void exit() throws Exception {
		if (server != null) {
				server.stop();
		}
	}

	@Test
	public void testApp() throws IOException {
		final String imageName = "image1.png";
		final String httpBaseUrl = "http://"+HOST+":"+PORT+server.getPath();
		File testClassesDir = ImageDownloader
				.getTestClassesDir(ImageDownloadTest.class);
		ImageDownloader.download(testClassesDir, httpBaseUrl, imageName);
		assertTrue((new File(testClassesDir, imageName)).exists());

	}

}
