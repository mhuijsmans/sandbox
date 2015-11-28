package org.mahu.proto.jnitest.nativewrapper;

public class HelloJNI {
	
	// used in JNI
	public int test = 0;
	
	public void setIntValue(int i) {
		test = i;
	}
	
	public int getIntValue() {
		return test;
	}	

	/**
	 * Print hello world to standard out
	 */
	public native void printHello();
	
	/**
	 * return "Hello" + received inoput string.
	 * @param input
	 * @return
	 */
	public native String addHelloPrefix(String input);

	/**
	 * Add the input values and return result
	 * @param i
	 * @param j
	 * @return
	 */
	public native int addValues(int i, int j);

	/**
	 * Return array of length bs.length+1 with values 3,4,.... 
	 * @param bs
	 * @return
	 */
	public native byte[] processBytes(byte[] bs);
	

	/**
	 * Provide a object to JNI that can be read and updated.
	 * @param dc
	 */
	public native void processDataClass(final DataClass dc);
	
	/**
	 * Provide a object to JNI that can be read and updated.
	 * @param dc
	 */
	public native void processDataClass1(final DataClass1 dc1);	
	
	/**
	 * Provide a object to JNI that can be read and updated
	 * @param dc
	 */
	public native DataClass returnDataClass();
	
	
	public byte[] specialBytes;
	/*
	 * Testing special cases
	 */
	public native int testSpecial(int i);	
	
	/*
	 * Testing error cases
	 */
	public native int testErrorCase1(int i);
	
	/*
	 * Testing error cases
	 */
	public native int testErrorCase2(int i, String className, String fieldOrMethodName,  String fieldOrMethodSignature,  Object fieldOrMethodObject);
	
	/*
	 * Testing logging
	 */	
	public native int logging(LoggingInterface log, int test);
}
