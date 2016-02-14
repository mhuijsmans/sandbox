package org.mahu.proto.lifecycle.impl;

import org.mahu.proto.lifecycle.ILifeCycleManager.LifeCycleState;

class ServiceStartTask extends LifeCycleTask implements Runnable {
    
    ServiceStartTask(final LifeCycleTaskContext context) {
        super(context);
    }

    @Override
    public void run() {
        if (isState(LifeCycleState.init) || isState(LifeCycleState.running)) {
            try {
                getContext().createNewServiceLifeCycleControl();
                guardedExecution(() -> startServices(), ServiceStartTask.class);
                setState(LifeCycleState.running);
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