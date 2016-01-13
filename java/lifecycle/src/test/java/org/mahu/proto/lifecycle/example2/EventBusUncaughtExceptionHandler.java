package org.mahu.proto.lifecycle.example2;

import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;

// See: http://docs.guava-libraries.googlecode.com/git/javadoc/com/google/common/eventbus/EventBus.html
// Handling Throwables and exceptions that escape our application code is something that needs to be added.
public class EventBusUncaughtExceptionHandler implements SubscriberExceptionHandler {

    // It appears that this does not catch ERROR, or any other Throwable type
    // than Exception.
    
    private AtomicInteger exceptionCounter = new AtomicInteger();

    @Override
    public void handleException(final Throwable throwable, final SubscriberExceptionContext context) {
        incrCounter();
    }
    
    public int getExceptionCounter() {
        return exceptionCounter.get(); 
    }
    
    private void incrCounter() {
        exceptionCounter.incrementAndGet();
    }
}
