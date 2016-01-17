package org.mahu.proto.lifecycle.example2;

import org.mahu.proto.lifecycle.IEventBus;
import org.mahu.proto.lifecycle.impl.LifeCycleServiceBase;

import com.google.inject.Inject;

public class ExceptionInStartService extends LifeCycleServiceBase {
    
    public final static String EXCEPTION_MSG = "ExceptionInStartService.exception";

    @Inject
    public ExceptionInStartService(final IEventBus eventBus) {
        super(eventBus);
    }
    
    @Override
    public void start() {
        throw new RuntimeException(EXCEPTION_MSG);
    }
    

}
