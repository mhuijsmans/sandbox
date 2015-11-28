package org.mahu.proto.commons;

public class StopWatch {
	private final long startTimeMS = Now();
	private long clocked = 0;

	public static long Now() {
		return System.currentTimeMillis();
	}
	
	public void Clock () {
		clocked = Now();
	}

	public long ElapsedTimeInMS() {
		long endTime = clocked==0 ? Now() : clocked;
		return endTime - startTimeMS;
	}

}
