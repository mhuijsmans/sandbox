package org.mahu.rpm.rpm_jni;

import java.util.logging.Logger;

import org.junit.Test;

public class JniLibraryUnitTest 
{
	private final static Logger LOGGER = Logger.getLogger(JniLibraryUnitTest.class.getName());
	
	@Test
	public void testUnit() {
		LOGGER.info("unit test");
		// Next to line always return null. Unclear why.
		// See pom for more details.
		LOGGER.info("library.typeOfTest    : "+System.getenv("LIBRARY_TYPEOFTEST"));
		LOGGER.info("library.builddirectory: "+System.getenv("LIBRARY_TYPEOFTEST"));
		LibraryUtil.printLibraryPath();
	}		
	
}
