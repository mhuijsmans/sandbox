package org.mahu.proto.socketthroughputtest;

import org.mahu.proto.commons.Chrono;

class DataCounter extends Const {
	Chrono chrono;
	private final String test;
	
	DataCounter(final String test) {
		this.test = test;
	}

	public void bytesRead(long cnt, int bytesRead) {

		if (cnt == 0) {
			chrono = new Chrono();
		} else {
			if ((cnt + bytesRead) == TOTAL_BYTES) {
				long elapsedMs = chrono.elapsedMs();
				long elapsedSec = elapsedMs / 1000;
				System.out.println(test + ", received (mb) " + TOTAL_MB
						+ " in (ms/sc) " + elapsedMs + "/" + elapsedSec
						+ " is mb/sec "
						+ (elapsedSec > 0 ? (TOTAL_MB / elapsedSec) : "?"));
			}
		}
	}
}
