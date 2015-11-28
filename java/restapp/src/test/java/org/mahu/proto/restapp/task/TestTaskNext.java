package org.mahu.proto.restapp.task;

import org.mahu.proto.restapp.model.ProcessTask;

public class TestTaskNext implements ProcessTask {

	@Override
	public Enum<?> execute() {
		return ProcessTask.Result.Next;
	}
}	
