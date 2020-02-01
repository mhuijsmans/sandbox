package org.mahu.proto.jersey.proxy.request;

import org.mahu.proto.jersey.proxy.inject.RequestProperties;

@RequestProperties
public interface IRequestWithoutBinding {

	String getResponse();

}
