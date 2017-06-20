package org.mahu.proto.lifecycle.impl;

import org.mahu.proto.lifecycle.ILifeCycleManager.LifeCycleState;

class ShutdownTask extends LifeCycleTask implements Runnable {

    ShutdownTask(final LifeCycleTaskContext context) {
        super(context);
    }

    @Override
    public void run() {
        try {
            switch (getState()) {
            case RUNNING:
                stopServices();
                break;
            case INIT:
                // In theory a shutdown can be called immediately after start
                // where an exception is thrown 
                abortServices();
                break;
            case FATAL:
            case SHUTDOWN:
            default:
            }
        } catch (Throwable t1) {
            getContext().getStatus().incrExceptionCount();
            abortServicesCatchException();
        } finally {
            setState(LifeCycleState.SHUTDOWN);
        }
    }
    
    private void stopServices() {
        if (getServiceLifeCycleControl().isPresent()) {
            getServiceLifeCycleControl().get().stopServices();
        }
    }     

}