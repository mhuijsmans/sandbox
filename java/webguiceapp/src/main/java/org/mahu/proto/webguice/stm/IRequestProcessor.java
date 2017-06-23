package org.mahu.proto.webguice.stm;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;

// package scope
class IRequestProcessor implements IIRequestProcessor {

    private final IStateMachine stateMachine;
    private final Injector injector;

    @Inject
    IRequestProcessor(final IStateMachine stateMachine, final Injector injector) {
        this.stateMachine = stateMachine;
        this.injector = injector;
    }

    @Override
    public Response execute(RequestType requestType, AbstractModule childModule) {
        final IRequestProvider requestProvider = new IRequestProvider(injector, childModule, requestType);
        try {
            // TODO: set request scope
            return stateMachine.execute(requestProvider);
        } finally {
            // TODO: clear requestScope
        }
    }

}
