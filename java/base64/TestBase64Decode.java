package org.mahu.base64;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import org.junit.Test;

public class TestBase64Decode {

	@Test
	public void testReadBase64EncodedPng() throws IOException {
		final String path = "C:/Users/310160231/Desktop/output";
		final String fileMask = path + "/bla.base64.xml";
		String content = new String(Files.readAllBytes(Paths.get(fileMask)));
		System.out.println("Filelength="+content.length());
		content = content.replace("\n", "");
		System.out.println("Filelength="+content.length());
		byte[] decoded = Base64.getDecoder().decode(content);
		System.out.println("png.Filelength="+decoded.length);
		String filePng = path +"/test.png";
		Files.write(Paths.get(filePng), decoded);
	}

}
