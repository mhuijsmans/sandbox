package org.mahu.proto.lifecycle.example2;

import java.util.Optional;

import org.mahu.proto.lifecycle.impl.ReadyAbortLock;

import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;

public class EventBusUncaughtExceptionHandler implements SubscriberExceptionHandler {

    // It appears that this does not catch ERROR, or any other Throwable type
    // than Exception.

    private int exceptionCounter = 0;
    private Optional<Throwable> lastThrownException = Optional.empty();
    private Optional<SubscriberExceptionContext> context = Optional.empty();
    private ReadyAbortLock lock = new ReadyAbortLock();

    @Override
    public void handleException(final Throwable throwable, final SubscriberExceptionContext context) {
        setThrowable(throwable, context);
    }

    public int getExceptionCounter() {
        synchronized (lock) {
            return exceptionCounter;
        }
    }

    public void waitForExceptionCaught(final int timeoutInMs) {
        synchronized (lock) {
            lock.wait3(timeoutInMs);
        }
    }

    public Optional<Throwable> getLastThrownException() {
        synchronized (lock) {
            return lastThrownException;
        }
    }

    public Optional<SubscriberExceptionContext> getSubscriberExceptionContext() {
        synchronized (lock) {
            return context;
        }
    }

    private void setThrowable(final Throwable t, final SubscriberExceptionContext subContext) {
        synchronized (lock) {
            exceptionCounter++;
            lastThrownException = Optional.ofNullable(t);
            context = Optional.ofNullable(subContext);
            lock.ready();
        }
    }
}
