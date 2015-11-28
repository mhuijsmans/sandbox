package org.mahu.proto.commons;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Class that generates a StackTrace and provides a string version. 
 * Useful for debugging to understand how a certain point was reached.
 */
public class StackTrace {
	
	public static void printStackTrace() {
		Exception e = getException();
		e.printStackTrace();
	}
	
	public static Exception getException() {
		Exception e = new Exception();
		e.fillInStackTrace();
		return e;
	}	
	
	public static void getStackTrace(final StringBuilder sb) {
		Exception e = getException();
		// todo: remove 2 topmost		
		sb.append(getStackTrace(e));
	}
	
	public static String getStackTrace() {
		Exception e = getException();
		// todo: remove 2 topmost
		return getStackTrace(e);
	}
	
	public static String getStackTrace(final Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		pw.flush();
		return sw.toString();
	}	
	
}
