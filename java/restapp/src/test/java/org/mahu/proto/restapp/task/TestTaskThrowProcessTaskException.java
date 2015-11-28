package org.mahu.proto.restapp.task;

import org.mahu.proto.restapp.model.ProcessTask;

public class TestTaskThrowProcessTaskException implements ProcessTask {

		@Override
		public Enum<?> execute() throws ProcessTaskException {
			RuntimeException cause = new RuntimeException();
			throw new ProcessTask.ProcessTaskException("caught exception",
					cause);
		}
	}