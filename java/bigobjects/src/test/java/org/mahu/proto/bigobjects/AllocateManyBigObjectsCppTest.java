package org.mahu.proto.bigobjects;

import org.junit.Test;

/**
 * This test is executed in a separate process, started from the Maintest.
 */
public class AllocateManyBigObjectsCppTest extends ProcessTestBase {

	static {
		printLibraryPath();
		System.loadLibrary("bigobject");
	}

	@Test
	public void testApp() throws InterruptedException {
		printMemoryInfo();
		AllocateUtil.allocateNativeObject(800);
		AllocateUtil.allocateBigJavaObjectsUntilOutOfMemory();
		//Thread.sleep(30*1000);
	}	

}
