package org.mahu.proto.lifecycle.example2;

import java.lang.Thread.UncaughtExceptionHandler;

import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;

public class EventBusUncaughtExceptionProxy implements SubscriberExceptionHandler {

    private final UncaughtExceptionHandler uncaughtExceptionHandler;
    
    public EventBusUncaughtExceptionProxy(final UncaughtExceptionHandler uncaughtExceptionHandler) {
        this.uncaughtExceptionHandler = uncaughtExceptionHandler;
    }
   
    /**
     * Guava EventBus doesn't catch all events, Error's aren't caught. They are caught by
     * UncaughtExceptionHandler of the thread;
     */
    
    @Override
    public void handleException(final Throwable throwable, final SubscriberExceptionContext context) {
        // Log error
        uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), throwable);
    }    
}
