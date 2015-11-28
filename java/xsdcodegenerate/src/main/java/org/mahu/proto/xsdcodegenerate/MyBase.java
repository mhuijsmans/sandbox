package org.mahu.proto.xsdcodegenerate;

public class MyBase {

	protected final static boolean DEBUG = false;
	
	protected final static String GET = "get";
	protected final static String SET = "set";

	protected void assertTrue(boolean b, final String msg) {
		if (!b) {
			throw new AssertionError(msg);
		}
	}

	protected void println(final String format, Object... args) {
		System.out.println(String.format(format, args));
	}

	protected void println(final Object msg) {
		System.out.println(msg);
	}

}
