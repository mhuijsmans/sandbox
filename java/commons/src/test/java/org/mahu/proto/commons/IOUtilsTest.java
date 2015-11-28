package org.mahu.proto.commons;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class IOUtilsTest {
	@Test
	public void testApp() throws IOException {
		final String filename = "test.txt";
		File datain = new File(getTestClassesDir(), "datain/" + filename);
		byte[] bytesIn = IOUtils.readAllBytes(datain);

		File dirDataout = new File(getTargetDir(), "dataout");
		if (dirDataout.exists()) {
			dirDataout.delete();
		}
		File dataout = new File(dirDataout, filename);
		IOUtils.writeBytes(dataout, bytesIn);
		assertTrue(dataout.exists());
		assertTrue(dataout.length() == datain.length());
		//
		byte[] bytesOut = IOUtils.readAllBytes(dataout);
		assertTrue(bytesIn.length == bytesOut.length);
		
	}
	
	public File getTargetDir() {
		File file = IOUtils.getResourceFile(this.getClass(), ".");
		return file.getParentFile();
	}
	
	public File getTestClassesDir() {
		return new File(getTargetDir(), "test-classes");
	}	
}
