package org.mahu.proto.restapp.task;

import javax.inject.Inject;

import org.mahu.proto.restapp.model.ProcessTask;

public class TestTaskNextWithAnnotatedClass implements ProcessTask {
	
	@Inject
	private TestConfigData configData;

	@Override
	public Enum<?> execute() throws ProcessTaskException {
		if (configData==null) {
			throw new ProcessTaskException("configdata not set");
		}
		return ProcessTask.Result.Null;
	}
}	
