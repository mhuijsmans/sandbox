package org.mahu.proto.restapp.model;

/**
 * By implementing this interface a ProcessTask implementation once invoked, can
 * return a result.Pauze to indicate that execution shall be paused. Later
 * execution can be resumed.
 */
public interface PausableTask {

	public enum Result {
		Pauze // Used by instance to indicate that executed is paused till later
				// moment
	}

	/**
	 * Cancel is called when the session is aborted
	 */
	public void cancel();

	public boolean equals(Object obj);

}
