package org.mahu.proto.restapp.task;

import org.mahu.proto.restapp.model.ProcessTask;

/**
 * Class that can be used as EndTask or it can be extended by a EndTask with logic.
 */
public final class EndTask implements ProcessTask {

	@Override
	public Enum<?> execute() {	
		return ProcessTask.Result.Null;
	}

}