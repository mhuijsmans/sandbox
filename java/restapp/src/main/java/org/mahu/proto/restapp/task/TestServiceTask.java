package org.mahu.proto.restapp.task;

import javax.inject.Inject;
import javax.inject.Named;

import org.mahu.proto.restapp.model.CloseableTask;
import org.mahu.proto.restapp.model.Node;
import org.mahu.proto.restapp.model.ProcessTask;
import org.mahu.proto.restapp.service.Service;

public class TestServiceTask implements ProcessTask, CloseableTask {
	
	@Inject
	Node node;
	
	@Inject
	@Named("testservice")
	Service testservice;
	
	@Override
	public Enum<?> execute() throws ProcessTaskException {
		System.out.println("TestServiceTask: node="+node.getName());
		try {
			testservice.Start();
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			throw new ProcessTaskException(e);
		}
		return ProcessTask.Result.Next;
	}

	@Override
	public void close() {
		testservice.Close();		
	}

}