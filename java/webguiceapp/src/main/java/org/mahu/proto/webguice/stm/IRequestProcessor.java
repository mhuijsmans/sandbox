package org.mahu.proto.webguice.stm;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;

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
        return stateMachine.execute(requestProvider);
    }

}
