package org.mahu.proto.lifecycle.impl;

import org.mahu.proto.lifecycle.ILifeCycleManager.LifeCycleState;

class ServiceStartTask extends LifeCycleTask implements Runnable {
    
    ServiceStartTask(final LifeCycleTaskContext context) {
        super(context);
    }

    @Override
    public void run() {
        if (isState(LifeCycleState.INIT) || isState(LifeCycleState.RUNNING)) {
            try {
                getContext().createNewServiceLifeCycleControl();
                guardedExecution(() -> startServices(), ServiceStartTask.class);
                setState(LifeCycleState.RUNNING);
            } finally {
                getContext().getStatus().incrServiceStartCount();
            }
        }
        // When received in other state, ignore
    }
    
    private void startServices() {
        getContext().getServiceLifeCycleControl().get().startServices();
    }    
    
}