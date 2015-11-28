package org.mahu.proto.restapp.task;

import org.mahu.proto.restapp.model.ProcessTask;
import org.mahu.proto.restapp.model.annotation.ProcessTaskResult;

public class InvalidResultValueTask implements ProcessTask {
	@ProcessTaskResult
	public enum Result {
		OK, NOK
	}

	public enum Result1 {
		OK, NOK
	}

	@Override
	public Enum<?> execute() throws ProcessTaskException {
		// return invalid result
		return Result1.OK;
	}
}