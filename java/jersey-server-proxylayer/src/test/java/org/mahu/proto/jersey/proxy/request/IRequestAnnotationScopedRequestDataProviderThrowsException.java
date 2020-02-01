package org.mahu.proto.jersey.proxy.request;

import org.mahu.proto.jersey.proxy.inject.RequestProperties;

@RequestProperties(requestScopedMapProvider=RequestScopeMapProviderThrowsException.class)
public interface IRequestAnnotationScopedRequestDataProviderThrowsException {
	
	String getResponse();

}
