package org.mahu.proto.lifecycle.impl;

import java.lang.Thread.UncaughtExceptionHandler;

class LifeCycleManagerUncaughtExceptionHandler implements UncaughtExceptionHandler {
    
    private final LifeCycleTaskContext context;    
    
    LifeCycleManagerUncaughtExceptionHandler(final LifeCycleTaskContext context) {
        this.context = context;
    }
    
    // TODO: logging of reported exception
    
    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        context.getStatus().incrExceptionCount();
        // Notification from another thread about an UncaughtException.
        // Exception can be from the LifeCycleManagerThread.
        forward(thread, throwable);
    }
    
    public void forwardedFirstUncaughtException(final int id, final Thread thread, final Throwable throwable) {
        context.getStatus().incrForwardedExceptionCount();
        // Notification from another thread about an UncaughtException.
        // Exception is from one of the EventBusThreads.
        // Multiple EventBus threads can throw an exception at the same time.
        // The first will be delivered to this method.
        forward(thread, throwable);
    }
    
    public void forwardNextUncaughtException(final int id, final Thread thread, final Throwable throwable) {
        context.getStatus().incrForwardedExceptionCount(); 
    }

    private void forward(Thread thread, Throwable throwable) {
        final String providedName = ThreadFactoryFactory.getProvidedName(thread);        
        context.getExecutorService().asyncExecute(new UncaughtExceptionTask(context, providedName, throwable)); 
    }    
}
