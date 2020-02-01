package org.mahu.proto.jersey.proxy.request;

import com.google.inject.AbstractModule;
import com.google.inject.servlet.ServletScopes;

public final class RequestScopedDataModule extends AbstractModule  {

	@Override
	public void configure() {
		bind(IRequestWithRequestScopedDataOverrule.class).to(RequestWithRequestScopedDataOverrule.class);
		bind(IRequestWithRequestScopedDataDefault.class).to(RequestWithRequestScopedDataDefault.class);
		bind(IRequestScopedData.class).to(RequestScopedDataDefault.class).in(ServletScopes.REQUEST);
	}

}
