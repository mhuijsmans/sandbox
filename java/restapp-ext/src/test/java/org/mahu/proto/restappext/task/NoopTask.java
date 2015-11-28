package org.mahu.proto.restappext.task;

import org.mahu.proto.restapp.model.ProcessTask;

public class NoopTask implements ProcessTask {

	@Override
	public Enum<?> execute() {
		return ProcessTask.Result.Next;
	}

}