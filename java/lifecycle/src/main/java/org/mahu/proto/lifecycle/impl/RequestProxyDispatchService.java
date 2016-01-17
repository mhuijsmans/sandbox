package org.mahu.proto.lifecycle.impl;

import org.mahu.proto.lifecycle.IEventBus;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

public class RequestProxyDispatchService extends LifeCycleServiceBase {

    @Inject
    public RequestProxyDispatchService(final IEventBus eventBus) {
        super(eventBus);
    }

    @Subscribe
    public void process(RequestProxyEvent event) {
        verifyServiceIsActive();
        event.execute();
    }

}
