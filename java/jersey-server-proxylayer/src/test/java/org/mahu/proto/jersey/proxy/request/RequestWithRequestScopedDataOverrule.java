package org.mahu.proto.jersey.proxy.request;

import javax.inject.Inject;

public class RequestWithRequestScopedDataOverrule implements IRequestWithRequestScopedDataOverrule {
	
	private final IRequestScopedData requestBladibla;

	@Inject
	RequestWithRequestScopedDataOverrule(IRequestScopedData requestBladibla) {
		this.requestBladibla = requestBladibla;
	}

	@Override
	public String getResponse() {
		return requestBladibla.getInfo();
	}

}
