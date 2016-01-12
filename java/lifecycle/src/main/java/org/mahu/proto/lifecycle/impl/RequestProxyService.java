package org.mahu.proto.lifecycle.impl;

import org.mahu.proto.lifecycle.ILifeCycleService;
import org.mahu.proto.lifecycle.example2.EventLog;
import org.mahu.proto.lifecycle.example2.EventLog.Event;
import org.mahu.proto.lifecycle.example2.IEventBus;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

public class RequestProxyService implements ILifeCycleService {

    final IEventBus eventBus;

    @Inject
    public RequestProxyService(final IEventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void start() {
        EventLog.log(Event.start, this);
        eventBus.register(this);
    }

    @Override
    public boolean stop() {
        EventLog.log(Event.stop, this);
        eventBus.unregister(this);
        return true;
    }

    @Override
    public void abort() {
        EventLog.log(Event.abort, this);
        eventBus.unregister(this);
    }

    @Subscribe
    public void process(RequestProxyEvent event) {
        event.execute();
    }

}
