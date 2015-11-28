package org.mahu.proto.commons;

/**
 * Class that supports guarded/defensive programming.
 */
public class Assert {
	
	/**
	 * Use this method on places where you have not put code yet.  
	 */
	public static void notImplemented() {
		System.err.println("Not Implemented!!\n"+StackTrace.getStackTrace());
		System.exit(1);
	}

}
