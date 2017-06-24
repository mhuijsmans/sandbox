package org.mahu.proto.webguice.request;

import org.mahu.proto.webguice.stm.IRequest;

import com.google.inject.AbstractModule;

// Via this binding module, the IRequest is bound to GetRequest.
public class GetRequestBindingModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new RequestCommonBindingsModule());
        bind(IRequest.class).to(GetRequest.class);
    }
}
