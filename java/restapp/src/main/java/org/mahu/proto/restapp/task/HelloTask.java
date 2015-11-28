package org.mahu.proto.restapp.task;

import javax.inject.Inject;

import org.mahu.proto.restapp.model.Node;
import org.mahu.proto.restapp.model.ProcessTask;

public class HelloTask implements ProcessTask {
	
	@Inject
	Node node;

	@Override
	public Enum<?> execute() throws ProcessTaskException {
		System.out.println("HelloTask: node="+node.getName());
		return ProcessTask.Result.Next;
	}

}