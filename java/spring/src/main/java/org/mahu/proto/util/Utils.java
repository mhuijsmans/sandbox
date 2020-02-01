package org.mahu.proto.util;

import java.util.concurrent.atomic.AtomicLong;

public final class Utils {
	
	public static String now() {
		return Long.toString(System.currentTimeMillis());
	}
	
	private static AtomicLong cntr = new AtomicLong();
	public static String nextId() {
		return Long.toString(cntr.incrementAndGet());
	}	

}
