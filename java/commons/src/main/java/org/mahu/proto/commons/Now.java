package org.mahu.proto.commons;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Now {
	
	private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	
	/**
	 * @return current date formatted as yyyy-MM-dd HH:mm:ss.SSS
	 */
	public static String asString() {
		return sdf.format(new Date());
	}

	/**
	 * @return current time in MS (using System.currentTimeMillis())
	 */
	public static long now() {
		return System.currentTimeMillis();
	}
}
