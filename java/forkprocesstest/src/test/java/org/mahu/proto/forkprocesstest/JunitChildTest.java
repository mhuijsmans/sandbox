package org.mahu.proto.forkprocesstest;

import org.junit.Test;

public class JunitChildTest extends ProcessTestBase {
// 	 at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.main(RemoteTestRunner.java:192)

	@Test
	public void testSpawnProcess() {
		System.err.println("!!!!! All is ok, just printing a stack trace !!!!!");
		Exception e = new Exception();
		e.fillInStackTrace();
		e.printStackTrace();
	}
}
