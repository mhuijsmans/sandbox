package org.mahu.proto.workmanager;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class WorkManager {

	private static Logger LOG = WorkManagerLogger.LOGGER;

	private long nrOfExecutedJobs = 0;
	private long nrOfPostedJobs = 0;
	private int nrOfActiveJobs = 0;

	private int maxNrOfActiveJobsPerPeriod = 4;
	private int nrOfJobsExecutedLastPeriod = 0;
	private ExecutorService executor = Executors.newFixedThreadPool(16);
	private final JobThroughputCalculator throughputCalculator;
	private List<Runnable> jobQueue = new LinkedList<Runnable>();

	public WorkManager(int samplePeriodInSec) {
		if (samplePeriodInSec < 1) {
			throw new IllegalArgumentException(
					"samplePeriodInMs is to low, must be >= 1");
		}
		throughputCalculator = new JobThroughputCalculator(
				samplePeriodInSec * 1000);
	}

	public int getNrOfActiveThreads() {
		return maxNrOfActiveJobsPerPeriod;
	}

	public synchronized void execute(Runnable job) {
		startThroughputCalculator();
		addJobToQueue(job);
		if (nrOfActiveJobs < maxNrOfActiveJobsPerPeriod) {
			addAdditionalJob();
		}
	}

	public synchronized void waitForAllJobsToTerminate() {
		try {
			while (nrOfExecutedJobs < nrOfPostedJobs) {
				wait(1000);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void terminate() throws InterruptedException {
		// hard shutdown. Posted jobs in executor will be list;
		executor.shutdownNow();
		executor.awaitTermination(throughputCalculator.samplePeriodInMs + 1000,
				TimeUnit.MILLISECONDS);
	}

	public synchronized long getNrOfExecutedJobs() {
		return nrOfExecutedJobs;
	}

	public synchronized void completedJob() {
		LOG.info("Job completed nrOfActiveJobs: " + nrOfActiveJobs
				+ " queued: " + jobQueue.size() + " max: "
				+ maxNrOfActiveJobsPerPeriod);
		nrOfExecutedJobs++;
		if (nrOfActiveJobs <= maxNrOfActiveJobsPerPeriod) {
			if (scheduleFirstJobInList()) {
				return;
			}
		}
		LOG.info("Job--");
		nrOfActiveJobs--;
	}

	public void nrOfExecutedJobsThisPeriod(final int jobExecutedThisPeriod) {
		LOG.info("Jobs executed: last period: " + nrOfJobsExecutedLastPeriod
				+ " this period: " + jobExecutedThisPeriod);
		// more executed than last period ?
		if (jobExecutedThisPeriod > nrOfJobsExecutedLastPeriod) {
			int addJobs = (jobExecutedThisPeriod - nrOfJobsExecutedLastPeriod)/2;
			while (addJobs > 0) {
				addAdditionalJob();
				addJobs--;
				maxNrOfActiveJobsPerPeriod++;
			}
		}
		// less executed that last period ?
		if (jobExecutedThisPeriod < nrOfJobsExecutedLastPeriod) {
			if (maxNrOfActiveJobsPerPeriod > 1) {
				LOG.info("Job--");
				maxNrOfActiveJobsPerPeriod--;
			}
		}
		nrOfJobsExecutedLastPeriod = jobExecutedThisPeriod;
	}

	protected void addJobToQueue(Runnable job) {
		nrOfPostedJobs++;
		jobQueue.add(job);
	}

	private boolean addAdditionalJob() {
		if (scheduleFirstJobInList()) {
			LOG.info("Job++");
			nrOfActiveJobs++;
			return true;
		} else {
			return false;
		}
	}

	private boolean scheduleFirstJobInList() {
		if (jobQueue.size() > 0) {
			Runnable r = jobQueue.remove(0);
			executor.execute(new MonitoredRunnable(r));
			return true;
		}
		return false;
	}

	private void startThroughputCalculator() {
		if (nrOfPostedJobs == 0) {
			executor.execute(throughputCalculator);
		}
	}

	class MonitoredRunnable implements Runnable {
		private final Runnable target;

		MonitoredRunnable(Runnable target) {
			this.target = target;
		}

		public void run() {
			try {
				target.run();
			} finally {
				completedJob();
			}
		}

	}

	class JobThroughputCalculator implements Runnable {
		private final int samplePeriodInMs;
		private boolean isActive = true;

		JobThroughputCalculator(int samplePeriodInMs) {
			this.samplePeriodInMs = samplePeriodInMs;
		}

		void terminate() {
			isActive = false;
		}

		public void run() {
			LOG.info("ThroughputCalculator started");
			try {
				while (isActive) {
					long nrOfExecutedJobs = getNrOfExecutedJobs();
					TimeUnit.MILLISECONDS.sleep(samplePeriodInMs);
					int delta = (int) (getNrOfExecutedJobs() - nrOfExecutedJobs);
					nrOfExecutedJobsThisPeriod(delta);
				}
			} catch (InterruptedException e) {
				LOG.info("ThroughputCalculator terminated");
			}
		}

	}

}
