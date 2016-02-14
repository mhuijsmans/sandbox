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
            case running:
                stopServices();
                break;
            case init:
                // In theory a shutdown can be called immediately after start
                // where an exception is thrown 
                abortServices();
                break;
            case fatal:
            case shutdown:
            default:
            }
        } catch (Throwable t1) {
            getContext().getStatus().incrExceptionCount();
            abortServicesCatchException();
        } finally {
            setState(LifeCycleState.shutdown);
        }
    }
    
    private void stopServices() {
        if (getServiceLifeCycleControl().isPresent()) {
            getServiceLifeCycleControl().get().stopServices();
        }
    }     

}