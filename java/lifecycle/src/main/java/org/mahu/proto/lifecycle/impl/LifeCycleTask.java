package org.mahu.proto.lifecycle.impl;

import java.util.Optional;

import org.mahu.proto.lifecycle.ILifeCycleManager.LifeCycleState;
import org.mahu.proto.lifecycle.IServiceLifeCycleControl;

class LifeCycleTask {

    private final LifeCycleTaskContext context;

    public LifeCycleTask(final LifeCycleTaskContext context) {
        this.context = context;
    }

    protected void guardedExecution(final Runnable runnable, final Class<? extends LifeCycleTask> cls) {
        try {
            runnable.run();
        } catch (Throwable t) {
            throw new LifeCycleTaskException(t, cls);
        }
    }

    protected void abortServices() {
        if (getServiceLifeCycleControl().isPresent()) {
            getServiceLifeCycleControl().get().abortServices();
        }
    }

    protected void abortServicesCatchException() {
        try {
            abortServices();
        } catch (Throwable t) {
            // log
        }
    }

    protected LifeCycleTaskContext getContext() {
        return context;
    }

    public Optional<IServiceLifeCycleControl> getServiceLifeCycleControl() {
        return context.getServiceLifeCycleControl();
    }

    protected boolean isState(final LifeCycleState state) {
        return context.getStatus().getState() == state;
    }

    protected void setState(final LifeCycleState newState) {
        context.getStatus().setState(newState);
    }

    protected void shutdownNowExecutorService() {
        context.getExecutorService().shutdownNow();
    }

    protected LifeCycleState getState() {
        return context.getStatus().getState();
    }

    protected void asyncExecute(final Runnable task) {
        context.getExecutorService().asyncExecute(task);
    }
}
