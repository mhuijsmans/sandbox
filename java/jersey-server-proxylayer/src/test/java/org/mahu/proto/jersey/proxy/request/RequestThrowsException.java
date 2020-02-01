package org.mahu.proto.jersey.proxy.request;

final class RequestThrowsException implements IRequestThrowsException {

	public String getResponse() {
		throw new IllegalStateException();
	}

}
