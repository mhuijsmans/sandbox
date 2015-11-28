package org.mahu.proto.restapp.task;

import org.mahu.proto.restapp.model.ProcessTask;
import org.mahu.proto.restapp.model.annotation.ProcessTaskResult;

/**
 * Class that can be used as EndTask or it can be extended by a EndTask with logic.
 */
public final class TerminateTask implements ProcessTask {
	
	@ProcessTaskResult
	public enum Result {
		terminate
	}

	@Override
	public Enum<?> execute() {	
		return Result.terminate;
	}

}