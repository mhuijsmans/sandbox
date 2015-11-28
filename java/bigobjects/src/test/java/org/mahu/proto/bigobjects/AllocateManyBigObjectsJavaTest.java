package org.mahu.proto.bigobjects;

import org.junit.Test;

/**
 * This test is executed in a separate process, started from the Maintest.
 */
public class AllocateManyBigObjectsJavaTest extends ProcessTestBase {

	@Test
	public void testApp() {
		printMemoryInfo();
		AllocateUtil.allocateBigJavaObjectsUntilOutOfMemory();
	}
}
