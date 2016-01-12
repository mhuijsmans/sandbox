package org.mahu.proto.lifecycle.example2;

import org.mahu.proto.lifecycle.ILifeCycleService;
import org.mahu.proto.lifecycle.IPublicService;
import org.mahu.proto.lifecycle.PublicServiceKey;
import org.mahu.proto.lifecycle.impl.IPublicServiceKeyFactory;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

public class ErrorService implements ILifeCycleService, IPublicService<IErrorRequest>, IErrorRequest {

    private final IEventBus eventBus;
    private final IPublicServiceKeyFactory publicServiceKeyFactory;

    @Inject
    ErrorService(final IEventBus eventBus, final IPublicServiceKeyFactory publicServiceKeyFactory) {
        this.eventBus = eventBus;
        this.publicServiceKeyFactory = publicServiceKeyFactory;
    }

    @Override
    public void start() {
        eventBus.register(this);
    }

    @Override
    public boolean stop() {
        eventBus.unregister(this);
        return true;
    }

    @Override
    public void abort() {
        eventBus.unregister(this);
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
            eventBus.post(new UncaughtExceptionEvent());
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
