package org.mahu.proto.jersey.proxy.request;

import javax.inject.Inject;

public final class RequestWithRequestScopedDataDefault implements IRequestWithRequestScopedDataDefault {
	
	private final IRequestScopedData requestScopedData;

	@Inject
	RequestWithRequestScopedDataDefault(IRequestScopedData requestScopedData) {
		this.requestScopedData = requestScopedData;
	}

	@Override
	public String getResponse() {
		return requestScopedData.getInfo();
	}

}
