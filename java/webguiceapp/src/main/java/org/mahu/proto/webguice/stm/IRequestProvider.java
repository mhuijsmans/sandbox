package org.mahu.proto.webguice.stm;

import javax.inject.Provider;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;

public class IRequestProvider implements Provider<IRequest> {

    private final Injector injector;
    private final AbstractModule childModule;
    private final RequestType requestType;

    IRequestProvider(final Injector injector, final AbstractModule childModule, final RequestType requestType) {
        this.injector = injector;
        this.childModule = childModule;
        this.requestType = requestType;
    }

    @Override
    public IRequest get() {
        return childModule != null ? injector.createChildInjector(childModule).getInstance(IRequest.class)
                : injector.getInstance(IRequest.class);
    }

    public RequestType getRequestType() {
        return requestType;
    }

}
