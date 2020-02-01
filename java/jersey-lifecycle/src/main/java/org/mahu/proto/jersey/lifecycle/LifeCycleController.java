package org.mahu.proto.jersey.lifecycle;

import javax.inject.Inject;

public class LifeCycleController implements ILifeCycleInterface{
	
	private final IRequestPreProcessor requestPreProcesspor;
	
	@Inject
	LifeCycleController(final IRequestPreProcessor stateMachine) {
		this.requestPreProcesspor = stateMachine;
	}

	public void init() {
		requestPreProcesspor.getInstance(IBootRequest.class).execute();
		// async
		requestPreProcesspor.getInstance(IInitRequest.class).execute();
	}

}
