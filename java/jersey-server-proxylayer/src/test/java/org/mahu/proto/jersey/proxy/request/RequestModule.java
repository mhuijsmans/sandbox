package org.mahu.proto.jersey.proxy.request;

import com.google.inject.AbstractModule;

public final class RequestModule extends AbstractModule  {

	@Override
	public void configure() {
		bind(IRequestWithChildModule.class).to(RequestWithChildModule.class);		
		bind(IChildModuleInfo.class).to(ChildModuleInfo.class);
	}

}
