package org.mahu.proto.restapp.task;

import org.mahu.proto.restapp.model.ProcessTask;
import org.mahu.proto.restapp.model.annotation.ProcessTaskResult;

public class TestTaskOkNok implements ProcessTask {

	@ProcessTaskResult
	public enum Result {
		OK, NOK
	}

	@Override
	public Enum<?> execute() {
		System.out.println("TestTaskOkNok invoked, returning OK");
		return Result.OK;
	}
}