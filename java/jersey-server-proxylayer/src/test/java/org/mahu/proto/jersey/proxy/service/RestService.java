package org.mahu.proto.jersey.proxy.service;

import org.mahu.proto.jersey.proxy.inject.IRequestPreProcessor;
import org.mahu.proto.jersey.proxy.inject.RequestPreProcessor;
import org.mahu.proto.jersey.proxy.request.ApplicationModule;
import org.mahu.proto.jersey.proxy.request.IRequestNoArguments;
import org.mahu.proto.jersey.proxy.request.IRequestWithArgument;
import org.mahu.proto.jersey.proxy.request.IRequestWithChildModule;
import org.mahu.proto.jersey.proxy.request.IRequestWithRequestScopedDataOverrule;

import com.google.inject.Guice;

public class RestService implements IRestService {

	private static final ApplicationModule module = new ApplicationModule();
	private static final IRequestPreProcessor requestPreProcessor = new RequestPreProcessor(
			Guice.createInjector(module));

	@Override
	public String getInfo() {
		return requestPreProcessor.with(IRequestNoArguments.class).getResponse();
	}
	
	@Override
	public String getMoreInfo(final String input) {
		return requestPreProcessor.with(IRequestWithArgument.class).getResponse(input);
	}
	
	@Override
	public String getModuleInfo() {
		return requestPreProcessor.with(IRequestWithChildModule.class).getResponse();
	}		
	
	@Override
	public String getRequestSCopedInfo() {
		return requestPreProcessor.with(IRequestWithRequestScopedDataOverrule.class).getResponse();
	}	

	@Override
	public String getTruth() {
		return Const.TRUTH;
	}

}
