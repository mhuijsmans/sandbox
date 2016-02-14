package org.mahu.proto.lifecycle.impl;

import org.mahu.proto.lifecycle.ILifeCycleManager.LifeCycleState;

class ServicesRestartTask extends LifeCycleTask implements Runnable {

    ServicesRestartTask(final LifeCycleTaskContext context) {
        super(context);
    }

    @Override
    public void run() {
        if (isState(LifeCycleState.running)) {
            guardedExecution(() -> abortServices(), ServicesRestartTask.class);
            asyncExecute(new ServiceStartTask(getContext()));
        }
    }
}