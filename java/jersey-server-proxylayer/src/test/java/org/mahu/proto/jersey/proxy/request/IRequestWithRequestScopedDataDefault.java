package org.mahu.proto.jersey.proxy.request;

import org.mahu.proto.jersey.proxy.inject.RequestProperties;

@RequestProperties(moduleClass=RequestScopedDataModule.class)
public interface IRequestWithRequestScopedDataDefault {

	String getResponse();

}
