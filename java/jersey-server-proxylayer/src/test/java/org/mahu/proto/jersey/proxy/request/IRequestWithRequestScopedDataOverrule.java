package org.mahu.proto.jersey.proxy.request;

import org.mahu.proto.jersey.proxy.inject.RequestProperties;

@RequestProperties(moduleClass=RequestScopedDataModule.class, requestScopedMapProvider=RequestScopeMapProvider.class)
public interface IRequestWithRequestScopedDataOverrule {

	String getResponse();

}
