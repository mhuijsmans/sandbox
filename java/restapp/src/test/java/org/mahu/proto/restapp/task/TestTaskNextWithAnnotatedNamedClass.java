package org.mahu.proto.restapp.task;

import javax.inject.Inject;
import javax.inject.Named;

import org.mahu.proto.restapp.model.ProcessTask;

public class TestTaskNextWithAnnotatedNamedClass implements ProcessTask {
	
	public final static String NAMED_VALUE = "configdata";
	
	@Inject
	@Named(NAMED_VALUE)
	private TestConfigData configData;

	@Override
	public Enum<?> execute() throws ProcessTaskException {
		if (configData==null) {
			throw new ProcessTaskException("configdata not set");
		}
		return ProcessTask.Result.Null;
	}
}	
