package org.mahu.proto.startup;

import java.util.concurrent.Future;

public interface IApplicationInitializationRequest {
	
	public Future<Boolean> performInitialization();	

}
