package org.mahu.proto.junit;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

// http://junit.org/javadoc/4.11/
public class TestWatcherRuleTest {

	private static String watchedLog = "";

	/*
	 * The test watcher records outcome of all executed tests
	 */
	@Rule
	public TestWatcher watchman = new TestWatcher() {
		@Override
		protected void failed(Throwable e, Description description) {
			watchedLog += description + "\n";
		}

		@Override
		protected void succeeded(Description description) {
			watchedLog += description + " " + "success!\n";
		}
	};

	@Ignore
	@Test
	public void fails() {
		fail();
	}

	@Test
	public void succeeds() {
		assertTrue(true);
	}
}
