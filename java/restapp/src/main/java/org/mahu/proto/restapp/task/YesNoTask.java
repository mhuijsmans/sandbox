package org.mahu.proto.restapp.task;

import javax.inject.Inject;

import org.mahu.proto.restapp.model.Node;
import org.mahu.proto.restapp.model.ProcessTask;
import org.mahu.proto.restapp.model.annotation.ProcessTaskResult;

public class YesNoTask implements ProcessTask {
	
	@Inject
	Node node;
	
	@ProcessTaskResult
	public enum Result {
		YES, NO
	}
	

	@Override
	public Enum<?> execute() throws ProcessTaskException {
		System.out.println("YesNoTask: node="+node.getName());
		return Result.NO;
	}

}