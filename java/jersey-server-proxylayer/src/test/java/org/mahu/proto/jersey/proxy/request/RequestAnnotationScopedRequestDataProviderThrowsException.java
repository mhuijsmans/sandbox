package org.mahu.proto.jersey.proxy.request;

public class RequestAnnotationScopedRequestDataProviderThrowsException
		implements IRequestAnnotationScopedRequestDataProviderThrowsException {

	public String getResponse() {
		throw new IllegalStateException();
	}

}
