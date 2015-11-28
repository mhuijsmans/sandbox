package org.mahu.proto.jnitest;

import java.io.File;

import org.junit.Assert;

public class DynamicLibraryTestUtil {

	public static void copyDynamicLibraries() {
		String libName = "libtaskexecutor.so";
		LibraryUtil util = new LibraryUtil();
		File mavenBaseDir = util.getMavenBasedir();
		File srcLibraryDir = new File(mavenBaseDir, "src/main/cpp");
		util.copyLibraryToTarget(srcLibraryDir, libName);
		File destFile = new File(mavenBaseDir, "src/main/cpp/" + libName);
		Assert.assertTrue(destFile.exists());
	}

}
