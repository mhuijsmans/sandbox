package org.mahu.proto.jersey.proxy.request;

import org.mahu.proto.jersey.proxy.inject.RequestProperties;

@RequestProperties
public interface IRequestWithArgument {

	String getResponse(String input);

}
