package org.mahu.proto.junit;

import java.util.concurrent.TimeUnit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

public class TimeoutRuleTest {
	@Rule
	public Timeout globalTimeout = new Timeout(5000);

	@Test
	public void testInfiniteLoop() {
		// shall fail
		for (;;) {
			sleep1sec();
		}
	}

	static void sleep1sec() {
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			// ignore
		}
	}
}