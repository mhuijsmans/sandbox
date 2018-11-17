package org.mahu.proto.jarresource;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

import org.junit.Test;

public class AppTest {
	@Test
	public void testApp() throws UnsupportedEncodingException {
		getResourceAsFile("file1-1.txt");
		getResourceAsFile("data/file1-2.txt");
		System.out.println(JarResource1.class.getClassLoader().getResource(JarResource1.class.getName().replace('.', '/')+".class").toString());
		
		getResourceAsFile("file2-1.txt");
		getResourceAsFile("data/file2-2.txt");
		System.out.println(JarResource2.class.getClassLoader().getResource(JarResource2.class.getName().replace('.', '/')+".class").toString());
	}

	public static File getResourceAsFile(final String resourcePath) throws UnsupportedEncodingException {
		final URL inputDataFile = ClassLoader.getSystemResource(resourcePath);
		if (inputDataFile == null) {
			throw new RuntimeException("File not found "+ resourcePath);
		}
		System.out.println(inputDataFile.toString());		
		return new File(URLDecoder.decode(inputDataFile.getFile(), "UTF-8"));
	}
}
