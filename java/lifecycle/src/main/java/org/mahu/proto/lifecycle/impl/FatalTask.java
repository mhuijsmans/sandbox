package org.mahu.proto.lifecycle.impl;

import org.mahu.proto.lifecycle.ILifeCycleManager.LifeCycleState;

class FatalTask extends LifeCycleTask implements Runnable {
    
    private final boolean isEntryStateFinal;

    FatalTask(final LifeCycleTaskContext context) {
        super(context);
        isEntryStateFinal = isState(LifeCycleState.fatal);
        setState(LifeCycleState.fatal);
    }

    @Override
    public void run() {
        // To prevent looping, transition to fatal is called only once
        if (!isEntryStateFinal) {
            abortServices();
        }
    }

}
