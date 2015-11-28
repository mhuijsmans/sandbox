package org.mahu.proto.commons;

/**
 * Simple WatchStop 
 */
public class Chrono {

	private long start;

	public Chrono() {
		super();
		reset();
	}

	public void reset() {
		start = now();
	}

	private long now() {
		return System.currentTimeMillis();
	}
	
	public long elapsedMs() {
		return now() - start;
	}
	
	public String elapsedAndAvg(final int max) {
		long elapsed = elapsedMs();
		return "Elapsed=" + elapsed + " ms; max="+max+"; avg=" + ((elapsed +(max-1))/ max)+" ms";
	}

}
