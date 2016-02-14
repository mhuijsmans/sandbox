package org.mahu.proto.lifecycle.impl;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

import org.mahu.proto.lifecycle.IApiRegistry;
import org.mahu.proto.lifecycle.ILifeCycleManager;
import org.mahu.proto.lifecycle.IServiceLifeCycleControl;

public class LifeCycleManager implements ILifeCycleManager {

    private static final String GET_ACTIVESERVICECOUNT_ERROR = "Exception when retrieving activeServiceCount";

    private final LifeCycleTaskContext lifeCycleTaskContext;

    public LifeCycleManager(final IApiRegistry apiRegistry, final AbstractServiceModule moduleBindings) {
        lifeCycleTaskContext = new LifeCycleTaskContext(apiRegistry, moduleBindings);
    }

    @Override
    public void start() {
        asyncExecute(new ServiceStartTask(lifeCycleTaskContext));
    }

    @Override
    public void shutdown() {
        // First post the task, next tell the ExecutorService to shutdown.
        // Shutdown is performed after the ShutdownTask has executed.
        asyncExecute(new ShutdownTask(lifeCycleTaskContext));
        lifeCycleTaskContext.getExecutorService().shutdown();
    }

    @Override
    public LifeCycleManagerStatus getStatus() {
        return lifeCycleTaskContext.getStatus();
    }

    @Override
    public int getActiveServiceCount() {
        try {
            Future<Optional<IServiceLifeCycleControl>> serviceLifeCycleControlFuture = 
                    submit(() -> lifeCycleTaskContext.getServiceLifeCycleControl().isPresent()
                      ? Optional.of(lifeCycleTaskContext.getServiceLifeCycleControl().get()) : Optional.empty());
            Optional<IServiceLifeCycleControl> serviceLifeCycleControl = serviceLifeCycleControlFuture.get();
            return serviceLifeCycleControl.isPresent() ? serviceLifeCycleControl.get().getActiveServiceCount() : 0;
        } catch (InterruptedException | ExecutionException | RejectedExecutionException e) {
            throw new RuntimeException(GET_ACTIVESERVICECOUNT_ERROR, e);
        }
    }

    private void asyncExecute(final Runnable task) {
        lifeCycleTaskContext.getExecutorService().asyncExecute(task);
    }
    
    <T> Future<T> submit(Callable<T> task) {
        return lifeCycleTaskContext.getExecutorService().submit(task);
    }

}
