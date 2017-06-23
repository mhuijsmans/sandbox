package org.mahu.proto.webguice.restclient;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

// Via this binding module, the IRequest is bound to GetRequest.
public class RestClientModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new FactoryModuleBuilder().implement(IRestClient.class, RestClient.class)
                .build(IRestClientFactory.class));
    }
}
