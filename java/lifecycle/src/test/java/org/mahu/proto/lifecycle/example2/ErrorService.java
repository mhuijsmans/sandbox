package org.mahu.proto.lifecycle.example2;

import org.mahu.proto.lifecycle.IPublicService;
import org.mahu.proto.lifecycle.IPublicServiceKeyFactory;
import org.mahu.proto.lifecycle.PublicServiceKey;
import org.mahu.proto.lifecycle.impl.LifeCycleServiceBase;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

public class ErrorService extends LifeCycleServiceBase implements IPublicService<IErrorRequest>, IErrorRequest {

    private final IPublicServiceKeyFactory publicServiceKeyFactory;

    @Inject
    ErrorService(final IEventBus eventBus, final IPublicServiceKeyFactory publicServiceKeyFactory) {
        super(eventBus);
        this.publicServiceKeyFactory = publicServiceKeyFactory;
    }

    // IPublicService
    @Override
    public PublicServiceKey<IErrorRequest> getPublicServiceKey() {
        return publicServiceKeyFactory.createKey(IErrorRequest.class, this);
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
