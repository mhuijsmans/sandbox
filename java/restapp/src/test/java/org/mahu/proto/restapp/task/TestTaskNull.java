package org.mahu.proto.restapp.task;

import org.mahu.proto.restapp.model.ProcessTask;

public class TestTaskNull implements ProcessTask {

	@Override
	public Enum<?> execute() {
		return ProcessTask.Result.Null;
	}
}

