package org.mahu.proto.imagedownload;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ImageDownloader {
	public final static int MAX_WAIT_IN_MS = 5000; // set timeout to 5 seconds

	public static void download(final File targetDir, final String httpBaseUrl,
			final String imageName) throws IOException {
		URL url = new URL(httpBaseUrl + "/" + imageName);
		System.out.println("URL: "+url);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setConnectTimeout(MAX_WAIT_IN_MS);
		con.setRequestMethod("GET");
		con.connect();
		int responseCode = con.getResponseCode();
		assertEquals(HttpURLConnection.HTTP_OK, responseCode);
		//
		int length = con.getContentLength();
		InputStream is = null;
		try {
			is = con.getInputStream();
			byte[] b = new byte[length];
			if (is.read(b) != length) {
				throw new IOException("Failed to read all data");
			}
			writeBytes(new File(targetDir, imageName), b);
		} finally {
			close(is);
		}
	}
	
	public static File getTestClassesDir(final Class<?> testClass) {
		return getResourceFile(testClass, ".");
	}
	
	public static URL getResourceUrl(final Class<?> cls,
			final String resourceName) {
		return cls.getClassLoader().getResource(resourceName);
	}

	public static File getResourceFile(final Class<?> cls,
			final String resourceName) {
		URL url = getResourceUrl(cls, resourceName);
		File f = new File(url.getFile());
		return f;
	}

	private final static void close(final InputStream is) {
		if (is != null) {
			try {
				is.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}

	private static void writeBytes(final File file, final byte[] b)
			throws IOException {
		File dir = file.getParentFile();
		if (!dir.exists()) {
			dir.mkdirs();
		}
		Path path = Paths.get(file.toURI());
		Files.write(path, b);
	}
}
