package org.mahu.proto.lifecycle.impl;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

import org.mahu.proto.lifecycle.IApiRegistry;
import org.mahu.proto.lifecycle.ILifeCycleManager;

public class LifeCycleManager implements ILifeCycleManager, UncaughtExceptionHandler {

    private static final String SERVICETHREAD_NAME = "LifeCycleManager.ServiceThread";

    private static final String ASYNC_EXCEPTION_DURING_STARTUP = "Exception in other thread executing started services";
    private static final String ERROR_INVALID_STATE = "Invalid state";
    private static final String EXCEPTION_DURING_STARTUP = "Exception starting up services";
    private static final String GET_ACTIVESERVICECOUNT_ERROR = "Exception when retrieving activeServiceCount";

    private static final long SHUTDOWNCOMPLETE_TIMEOUT = 30000;

    private final IApiRegistry apiRegistry;
    private final AbstractServiceModule moduleBindings;
    private final ExecutorService executorService;
    private final LifeCycleManagerStatus status;
    private final LifeCycleTaskContext lifeCycleTaskContext;

    // Data to be access in the ServiceThread
    static class LifeCycleTaskContext {
        private Optional<ServiceLifeCycleControl> serviceLifeCycleControl;
    }

    class StartupTask implements Runnable {

        StartupTask() {
            status.setState(LifeCycleState.startingUp);
        }

        @Override
        public void run() {
            lifeCycleTaskContext.serviceLifeCycleControl = Optional
                    .of(new ServiceLifeCycleControl(apiRegistry, moduleBindings));
            lifeCycleTaskContext.serviceLifeCycleControl.get().startServices();
            status.setState(LifeCycleState.running);
            status.incrServiceStartCount();
        }
    }

    class RestartTask implements Runnable {
        RestartTask() {
            status.setState(LifeCycleState.restarting);
        }

        @Override
        public void run() {
            lifeCycleTaskContext.serviceLifeCycleControl.get().abortServices();
            executorService.execute(new StartupTask());
        }
    }

    class FatalTask implements Runnable {
        private final boolean isEntryStateFinal;

        FatalTask() {
            isEntryStateFinal = status.getState() == LifeCycleState.fatal;
            status.setState(LifeCycleState.fatal);
        }

        @Override
        public void run() {
            // To prevent looping, abort of services in transition to fatal is
            // called only once
            if (!isEntryStateFinal && lifeCycleTaskContext.serviceLifeCycleControl.isPresent()) {
                try {
                    lifeCycleTaskContext.serviceLifeCycleControl.get().abortServices();
                } catch (Throwable t) {
                    // log, aborting of of the services failed.
                }
            }

        }
    }

    class ShutdownTask implements Runnable {

        @Override
        public void run() {
        }
    }

    class UncaughtExceptionTask implements Runnable {

        private final String providedThreadName;
        private final Throwable throwable;

        public UncaughtExceptionTask(String providedName, Throwable throwable) {
            this.providedThreadName = providedName;
            this.throwable = throwable;
        }

        @Override
        public void run() {
            if (providedThreadName.equals(SERVICETHREAD_NAME)) {
                // Exception in ServiceThread, which must? imply in a started
                // service
                executorService.execute(new FatalTask());
            } else {
                // Exception in EventBusThread
                switch (status.getState()) {
                case startingUp:
                    // exception during start of services
                    executorService.execute(new FatalTask());
                    break;
                case running:
                    // exception during start of services
                    executorService.execute(new RestartTask());
                    break;
                case restarting:
                    // State restarting is entered, because of uncaught
                    // exceptions. There can be multiple exceptions in sequence
                    // for a started set of service.
                case init:
                case fatal:
                case shutdown:
                default:
                    executorService.execute(new FatalTask());
                }
            }
        }

    }

    class GetServiceLifeCycleControlTask implements Callable<Optional<ServiceLifeCycleControl>> {

        @Override
        public Optional<ServiceLifeCycleControl> call() throws Exception {
            return lifeCycleTaskContext.serviceLifeCycleControl.isPresent()
                    ? Optional.of(lifeCycleTaskContext.serviceLifeCycleControl.get()) : Optional.empty();
        }

    }

    public LifeCycleManager(final IApiRegistry apiRegistry, final AbstractServiceModule moduleBindings) {
        this.apiRegistry = apiRegistry;
        this.moduleBindings = moduleBindings;
        executorService = ThreadFactoryFactory.createSingleThreadExecutor(SERVICETHREAD_NAME, this);
        status = new LifeCycleManagerStatus();
        lifeCycleTaskContext = new LifeCycleTaskContext();
        moduleBindings.setUncaughtExceptionHandler(LifeCycleManager.this);
    }

    @Override
    public void startUp() {
        verifyStateIs(LifeCycleState.init);
        executorService.execute(new StartupTask());
    }

    private void postFatalTask() {
        executorService.execute(new FatalTask());
    }

    private void verifyStateIs(final LifeCycleState requiredState) {
        if (!status.stateEquals(requiredState)) {
            throw new RuntimeException(ERROR_INVALID_STATE);
        }
    }

    @Override
    public void shutdown() {
        executorService.execute(new ShutdownTask());
        ExecutorUtils.shutdown(executorService);
    }

    @Override
    public LifeCycleManagerStatus getStatus() {
        return status;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        status.incrExceptionCount();
        // Notification from another thread about an UncaughtException.
        // There can be multiple exceptions.
        // Exception can be from the ServiceThread or EventBusThread
        final String providedName = ThreadFactoryFactory.getProvidedName(thread);
        executorService.execute(new UncaughtExceptionTask(providedName, throwable));
    }

    @Override
    public int getActiveServiceCount() {
        try {
            Future<Optional<ServiceLifeCycleControl>> serviceLifeCycleControlFuture  = executorService.submit(new GetServiceLifeCycleControlTask());
            Optional<ServiceLifeCycleControl> serviceLifeCycleControl;
            serviceLifeCycleControl = serviceLifeCycleControlFuture.get();
            return serviceLifeCycleControl.isPresent() ? serviceLifeCycleControl.get().getActiveServiceCount() : 0;
        } catch (InterruptedException | ExecutionException | RejectedExecutionException e) {
            throw new RuntimeException(GET_ACTIVESERVICECOUNT_ERROR, e);
        }
    }
}
