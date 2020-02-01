package org.mahu.proto.jersey.proxy.request;

import org.mahu.proto.jersey.proxy.inject.RequestProperties;

@RequestProperties(moduleClass=RequestModule.class)
public interface IRequestWithChildModule {

	String getResponse();

}
