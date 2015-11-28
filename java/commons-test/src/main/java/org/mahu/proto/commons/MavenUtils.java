package org.mahu.proto.commons;

import java.io.File;

public class MavenUtils {
	
	final File targetDir;
	
	public MavenUtils(final Object object) {
		this(object.getClass());
	}
	
	public MavenUtils(final Class<?> testClass) {
		File file = IOUtils.getResourceFile(testClass, ".");
		targetDir = file.getParentFile();
	}
	
	public File getTargetDir() {
		return targetDir;
	}
	
	public File getBaseDir() {
		return targetDir.getParentFile();
	}
	
	public File getClassesDir() {
		return new File(targetDir, "classes");
	}		
	
	public File getTestClassesDir() {
		return new File(targetDir, "test-classes");
	}	

}
