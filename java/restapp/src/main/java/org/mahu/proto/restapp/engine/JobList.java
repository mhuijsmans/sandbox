package org.mahu.proto.restapp.engine;

public interface JobList {

	/**
	 * @return returns number of jobs in the list
	 */		
	public int size();

	/**
	 * Add a job to the front of the queue 
	 * @job job
	 */			
	public void addFirst(final Job job);

	/**
	 * Add a job to the back of the queue 
	 * @job job
	 */			
	public void add(final Job job);

	/**
	 * @return Retrieves and remove, the first element of this list,
	 *         or returns null if this list is empty.
	 */	
	public Job getJob();

	/**
	 * @return Retrieves and remove, the first element of this list.
	 *         If there is no job, wait specified duration.
	 *         Returns null if this list is empty afetr the specified wait time.
	 */		
	public Job getJobAndWaitIfEmpty(int maxWaitInMs)
			throws InterruptedException;
	
	/**
	 * Wait for job to be available.
	 * @param maxWaitInMs max time in MS to wait for a Job
	 * @return true is job available, return false if no job available
	 */		
	public boolean WaitForJobAvailable(int maxWaitInMs)
			throws InterruptedException;

	/**
	 * @return Retrieves, but does not remove, the first element of this list,
	 *         or returns null if this list is empty.
	 */
	public Job front();
}
