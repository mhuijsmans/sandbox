package org.mahu.proto.jersey.proxy.request;

import com.google.inject.AbstractModule;
import com.google.inject.servlet.ServletScopes;

public class ApplicationModule extends AbstractModule {

	@Override
	public void configure() {
		bind(IRequestNoArguments.class).to(RequestNoArguments.class);
		bind(IRequestWithArgument.class).to(RequestWithArgument.class);
		bind(IRequestThrowsException.class).to(RequestThrowsException.class);
		bind(IRequestAnnotationScopedRequestDataProviderThrowsException.class)
				.to(RequestAnnotationScopedRequestDataProviderThrowsException.class);
	}

}
