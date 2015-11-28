package org.mahu.rpm.rpm_jni;

import java.util.logging.Logger;

import org.junit.Test;

public class JniLibraryStubTest 
{
	private final static Logger LOGGER = Logger.getLogger(JniLibraryStubTest.class.getName());	
			
	@Test
	public void testPrintHelloCanBeCalled() {
		LOGGER.info("integration STUB test");
		
		if (!LibraryUtil.isIntegrationTest()) {
			return;
		}
		
		LibraryUtil u = new LibraryUtil();
		u.copyLibrary("/repo_cpp/stublib","libcppnative.so");
		
		NativeHelloWorldPrinter app = new NativeHelloWorldPrinter();
		app.printHelloWorld();
	}
	
}
