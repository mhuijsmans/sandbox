package org.mahu.proto.webguice.request;

import org.mahu.proto.webguice.stm.IRequest;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class SomeRequestBindingModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new FactoryModuleBuilder().implement(IRequest.class, PostRequest.class)
                .build(SomeRequestFactory.class));
    }
}
