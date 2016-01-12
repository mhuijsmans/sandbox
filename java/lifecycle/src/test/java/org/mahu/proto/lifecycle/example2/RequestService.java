package org.mahu.proto.lifecycle.example2;

import org.mahu.proto.lifecycle.ILifeCycleService;
import org.mahu.proto.lifecycle.IPublicService;
import org.mahu.proto.lifecycle.PublicServiceKey;
import org.mahu.proto.lifecycle.example2.EventLog.Event;
import org.mahu.proto.lifecycle.impl.IPublicServiceKeyFactory;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

public class RequestService implements ILifeCycleService, IPublicService<ISessionRequest>, ISessionRequest {

    final IEventBus eventBus;
    final IPublicServiceKeyFactory publicServiceKeyFactory;

    @Inject
    public RequestService(final IEventBus eventBus, final IPublicServiceKeyFactory publicServiceKeyFactory) {
        this.eventBus = eventBus;
        this.publicServiceKeyFactory = publicServiceKeyFactory;
    }

    @Override
    public void start() {
        EventLog.log(Event.start, this);
        eventBus.register(this);
    }

    @Override
    public boolean stop() {
        EventLog.log(Event.stop, this);
        RequestServiceStopEvent stopEvent = new RequestServiceStopEvent();
        eventBus.post(stopEvent);
        return stopEvent.waitForStopped();
    }

    @Override
    public void abort() {
        EventLog.log(Event.abort, this);
        eventBus.unregister(this);
    }

    @Subscribe
    public void process(RequestServiceStopEvent event) {
        event.stopped();
        // Clean up while executing on the EventBus thread
        eventBus.unregister(this);
    }

    // IPublicService
    @Override
    public PublicServiceKey<ISessionRequest> getPublicServiceKey() {
        return publicServiceKeyFactory.createKey(ISessionRequest.class, this);
    }

    // ISessionRequest
    @Override
    public String process(String data) {
        EventLog.log(Event.event, this);
        return ISessionRequest.RESPONSE + data;
    }

    @Override
    public void throwResourceWithMessage(String exceptionMessage) {
        throw new SessionRequestException(exceptionMessage);
    }
    
    @Override
    public void waitOnLock(MeetUpLock lock) {
        lock.waitOnLockIsCalledWaitForContinue();
    }    

}
