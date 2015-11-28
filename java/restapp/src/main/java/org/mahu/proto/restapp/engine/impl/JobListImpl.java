package org.mahu.proto.restapp.engine.impl;

import java.util.LinkedList;

import org.mahu.proto.restapp.engine.Job;
import org.mahu.proto.restapp.engine.JobList;
import org.mahu.proto.restapp.engine.JobListProcessTaskFuture;

/**
 * Job's can be added from different threads, so class is synchronized.
 */
public class JobListImpl implements JobList, JobListProcessTaskFuture {

	private final LinkedList<Job> jobs = new LinkedList<Job>();
	private final Object lock;

	JobListImpl(Object lock) {
		this.lock = lock;
	}

	public int size() {
		synchronized (lock) {
			return jobs.size();
		}
	}

	@Override
	public void addFirst(final Job job) {
		synchronized (lock) {
			jobs.addFirst(job);
			lock.notify();
		}
	}

	@Override
	public void add(final Job job) {
		synchronized (lock) {
			jobs.add(job);
			lock.notify();
		}
	}

	@Override
	public Job getJob() {
		synchronized (lock) {
			if (!jobs.isEmpty()) {
				return jobs.remove(0);
			}
			return null;
		}
	}

	@Override
	public Job getJobAndWaitIfEmpty(final int maxWaitInMs)
			throws InterruptedException {
		synchronized (lock) {
			if (!jobs.isEmpty()) {
				return jobs.remove(0);
			}
			lock.wait(maxWaitInMs);
			if (!jobs.isEmpty()) {
				return jobs.remove(0);
			}
			return null;
		}
	}

	@Override
	public boolean WaitForJobAvailable(final int maxWaitInMs)
			throws InterruptedException {
		synchronized (lock) {
			if (!jobs.isEmpty()) {
				return true;
			}
			lock.wait(maxWaitInMs);
			if (!jobs.isEmpty()) {
				return true;
			}
		}
		return false;
	}

	public void clear() {
		synchronized (lock) {
			jobs.clear();
		}
	}

	public Job front() {
		synchronized (lock) {
			// peek also works when list is empty
			return jobs.peekFirst();
		}
	}

}
