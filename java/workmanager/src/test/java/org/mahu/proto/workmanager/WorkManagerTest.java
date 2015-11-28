package org.mahu.proto.workmanager;

import java.util.logging.Logger;

import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.mahu.proto.commons.Chrono;
import org.mahu.proto.workmanager.WorkManager;
import org.mahu.proto.workmanager.WorkManagerLogger;

public class WorkManagerTest {

	private static Logger LOG = WorkManagerLogger.LOGGER;

	private WorkManager workManager = null;

	@After
	public void beforeTestCase() {
		workManager = null;
	}

	@After
	public void afterTestCase() throws InterruptedException {
		if (workManager != null) {
			workManager.terminate();
		}
	}
	
	@Ignore
	@Test(timeout = 500)
	public void testTheSleepRunnable() {
		new SleepRunnable(350,0).run();
	}	

	// @Ignore
	@Test(timeout = 120000)
	public void testApp() {
		int workTimeInMs = 760;
		int max = 1000;
		Chrono chrono = new Chrono();
		workManager = new WorkManager(1);
		for (int i = 0; i < max; i++) {
			workManager.execute(new SleepRunnable(workTimeInMs, i));
		}
		workManager.waitForAllJobsToTerminate();
		//assert (Math.abs(workManager.getNrOfActiveThreads() - 5) <= 1);
		System.out.println(chrono.elapsedAndAvg(max));
		System.out.println(chrono.elapsedAndAvg(max));
		
	}

	static class SleepRunnable implements Runnable {
		private final int timeout;
		private final int id;

		SleepRunnable(int timeout, int id) {
			this.timeout = timeout;
			this.id = id;
		}

		public void run() {
			//LOG.info("SleepRunnable Enter "+id);
			Chrono chrono = new Chrono();
			while (chrono.elapsedMs() < timeout) {
				;
			}
			//LOG.info("SleepRunnable leave "+id);
		}

	}
}
