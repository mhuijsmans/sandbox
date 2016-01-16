package org.mahu.proto.lifecycle.example2;

import org.mahu.proto.lifecycle.IPublicService;
import org.mahu.proto.lifecycle.IPublicServiceKeyFactory;
import org.mahu.proto.lifecycle.PublicServiceKey;
import org.mahu.proto.lifecycle.impl.LifeCycleServiceBase;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

public class RequestService extends LifeCycleServiceBase implements  IPublicService<ISessionRequest>, ISessionRequest {

    final IPublicServiceKeyFactory publicServiceKeyFactory;

    @Inject
    public RequestService(final IEventBus eventBus, final IPublicServiceKeyFactory publicServiceKeyFactory) {
        super(eventBus);
        this.publicServiceKeyFactory = publicServiceKeyFactory;
    }

    // Called in LifeCycleService thread
    @Override
    public void stop() {
        final RequestServiceStopEvent stopEvent = new RequestServiceStopEvent();
        eventBusPost(stopEvent);
        if (stopEvent.waitForStopped()) {
            super.stop();
        } else {
            throw new RuntimeException("RequestServiceStopEvent wait timeout");
        };
    }

    // Called on EventBus thread
    @Subscribe
    public void process(RequestServiceStopEvent event) {
        event.stopped();
        // Clean up while executing on the EventBus thread
    }

    // IPublicService
    @Override
    public PublicServiceKey<ISessionRequest> getPublicServiceKey() {
        return publicServiceKeyFactory.createKey(ISessionRequest.class, this);
    }

    // ISessionRequest, called on EventBus thread
    @Override
    public String process(String data) {
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
