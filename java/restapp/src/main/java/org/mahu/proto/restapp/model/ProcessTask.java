package org.mahu.proto.restapp.model;

public interface ProcessTask {
	
	public static class ProcessTaskException extends Exception {

		private static final long serialVersionUID = -8271925236314452399L;
		
		public ProcessTaskException(final Exception exception) {
			super(exception);
		}		

		public ProcessTaskException(final String description) {
			super(description);
		}
		
		public ProcessTaskException(final String description, final Exception e) {
			super(description, e);
		}		
	}
	
	/**
	 * Predefined results that can be used 
	 */
	public enum Result {
		Null, // used by derived tasks that do not return a result, e.g. EndTask 
		Next,  // Used by task that has a single outcome
	}
	
	/**
	 * Execute task and return an Enum or null (if Task has no results, i.e. is and EndTask) 
	 * @return Result with outcome of execution
	 */
	public Enum<?> execute() throws ProcessTaskException;

}
