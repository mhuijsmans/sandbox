package org.mahu.proto.lifecycle.example2;

import org.mahu.proto.lifecycle.IEventBus;
import org.mahu.proto.lifecycle.IPublicService;
import org.mahu.proto.lifecycle.IPublicServiceKeyFactory;
import org.mahu.proto.lifecycle.PublicServiceKey;
import org.mahu.proto.lifecycle.impl.LifeCycleServiceBase;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

public class ErrorService extends LifeCycleServiceBase implements IPublicService<IErrorRequest>, IErrorRequest { 

    private final IPublicServiceKeyFactory publicServiceKeyFactory;
    private volatile boolean isThrowExceptionInStopService;

    @Inject
    ErrorService(final IEventBus eventBus, final IPublicServiceKeyFactory publicServiceKeyFactory) {
        super(eventBus);
        this.publicServiceKeyFactory = publicServiceKeyFactory;
        isThrowExceptionInStopService = false;
    }

    // IPublicService
    @Override
    public PublicServiceKey<IErrorRequest> getPublicServiceKey() {
        return publicServiceKeyFactory.createKey(IErrorRequest.class, this);
    }
    
    @Override
    public void stop() {
        if (isThrowExceptionInStopService) {
            throw new RuntimeException(IErrorRequest.MSG_EXCEPTION_IN_STOP_SERVICE);   
        }
        super.stop();
    }    

    // IErrorRequest
    @Override
    public String process(IErrorRequest.Test data) {
        switch (data) {
        case throwException:
            throw new RuntimeException(IErrorRequest.THROW_EXCEPTION_MSG);
        case causeUncaughtException:
            eventBusPost(new UncaughtExceptionEvent());
            return IErrorRequest.RESPONSE_OK;
        case causeTwoUncaughtExceptions:
            eventBusPost(new UncaughtExceptionEvent());
            eventBusPost(new UncaughtExceptionEvent());
            return IErrorRequest.RESPONSE_OK;  
        case throwExceptionInStopService:
            isThrowExceptionInStopService = true;
            return IErrorRequest.RESPONSE_OK;
        default:
            // Fall through, send default answer
        }
        return IErrorRequest.RESPONSE;
    }

    @Subscribe
    public void process(UncaughtExceptionEvent event) {
        throw new RuntimeException();
    }

}
