package org.mahu.proto.lifecycle.impl;

import org.mahu.proto.lifecycle.ILifeCycleService;
import org.mahu.proto.lifecycle.example2.EventLog;
import org.mahu.proto.lifecycle.example2.EventLog.Event;
import org.mahu.proto.lifecycle.example2.IEventBus;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

public class RequestProxyDispatchService implements ILifeCycleService {
    
    public static String SERVICE_UNAVAILABLE = "Requested service temporarily not available";

    boolean isRunning;
    final IEventBus eventBus;

    @Inject
    public RequestProxyDispatchService(final IEventBus eventBus) {
        this.eventBus = eventBus;
        isRunning = false;
    }

    @Override
    public void start() {
        isRunning = true;
        EventLog.log(Event.start, this);
        eventBus.register(this);
    }

    @Override
    public boolean stop() {
        isRunning = false;
        EventLog.log(Event.stop, this);
        eventBus.unregister(this);
        return true;
    }

    @Override
    public void abort() {
        isRunning = false;
        EventLog.log(Event.abort, this);
        eventBus.unregister(this);
    }

    @Subscribe
    public void process(RequestProxyEvent event) {
        if (isRunning) {
            event.execute();
        } else {
            throw new RuntimeException(SERVICE_UNAVAILABLE);
        }
    }

}
