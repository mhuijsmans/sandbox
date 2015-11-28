package org.mahu.rpm.rpm_jni;

import org.junit.Test;

public class AppTest 
{
	@Test
	public void testPrintHelloCanBeCalled() {;
		NativeHelloWorldPrinter app = new NativeHelloWorldPrinter();
		app.printHelloWorld();
	}
}
