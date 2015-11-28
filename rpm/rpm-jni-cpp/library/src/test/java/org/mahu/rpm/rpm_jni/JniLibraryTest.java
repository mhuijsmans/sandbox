package org.mahu.rpm.rpm_jni;

import java.util.logging.Logger;

import org.junit.Test;

public class JniLibraryTest 
{
	private final static Logger LOGGER = Logger.getLogger(JniLibraryTest.class.getName());
			
	@Test
	public void testPrintHelloCanBeCalled() {
		LOGGER.info("integration test");
		
		if (!LibraryUtil.isIntegrationTest()) {
			return;
		}
		
		LibraryUtil u = new LibraryUtil();
		u.copyLibrary("/repo_cpp/lib","libcppnative.so");
		
		NativeHelloWorldPrinter app = new NativeHelloWorldPrinter();
		app.printHelloWorld();
	}
	
}
