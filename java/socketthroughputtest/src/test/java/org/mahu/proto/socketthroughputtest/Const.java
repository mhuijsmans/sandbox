package org.mahu.proto.socketthroughputtest;

public class Const {
	
	protected final static int BUFFER_SIZE = 1024 * 64;
	protected static long MAX = 32 * 1024 * 8 * 2;
	protected final static long TOTAL_BYTES = BUFFER_SIZE * MAX;
	protected final static int ONE_MB = 1024 * 1024;
	protected final static long TOTAL_MB = TOTAL_BYTES / ONE_MB;

}
