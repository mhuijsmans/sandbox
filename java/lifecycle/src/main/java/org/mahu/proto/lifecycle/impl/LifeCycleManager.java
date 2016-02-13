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

    //private static final String ASYNC_EXCEPTION_DURING_STARTUP = "Exception in other thread executing started services";
    private static final String ERROR_INVALID_STATE = "Invalid state";
    //private static final String EXCEPTION_DURING_STARTUP = "Exception starting up services";
    private static final String GET_ACTIVESERVICECOUNT_ERROR = "Exception when retrieving activeServiceCount";

    //private static final long SHUTDOWNCOMPLETE_TIMEOUT = 30000;

    private final IApiRegistry apiRegistry;
    private final AbstractServiceModule moduleBindings;
    private final ExecutorService executorService;
    private final LifeCycleManagerStatus status;
    private final LifeCycleTaskContext lifeCycleTaskContext;

    // Data to be access in the ServiceThread
    static class LifeCycleTaskContext {
        private Optional<ServiceLifeCycleControl> serviceLifeCycleControl;
    }

    static class LifeCycleTaskException extends RuntimeException {
        private static final long serialVersionUID = 1L;
        private final Class<? extends LifeCycleTask> cls;

        LifeCycleTaskException(final Throwable t, final Class<? extends LifeCycleTask> cls) {
            super(t);
            this.cls = cls;
        }

        Class<? extends LifeCycleTask> getTaskClass() {
            return cls;
        }
    }

    class LifeCycleTask {
        protected void guardedExecution(final Runnable runnable, final Class<? extends LifeCycleTask> cls) {
            try {
                runnable.run();
            } catch (Throwable t) {
                throw new LifeCycleTaskException(t, cls);
            }
        }
    }

    class StartupTask extends LifeCycleTask implements Runnable {

        @Override
        public void run() {
            if (status.getState() == LifeCycleState.init || status.getState() == LifeCycleState.running) {
                lifeCycleTaskContext.serviceLifeCycleControl = Optional
                        .of(new ServiceLifeCycleControl(apiRegistry, moduleBindings));
                guardedExecution(() -> lifeCycleTaskContext.serviceLifeCycleControl.get().startServices(),
                        StartupTask.class);
                status.setState(LifeCycleState.running);
                status.incrServiceStartCount();
            }
        }
    }

    class RestartTask extends LifeCycleTask implements Runnable {
        @Override
        public void run() {
            if (status.getState() == LifeCycleState.running) {
                guardedExecution(() -> lifeCycleTaskContext.serviceLifeCycleControl.get().abortServices(),
                        RestartTask.class);
                executorServiceExecute(new StartupTask());
            }
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
                abortServices();
            }
        }

    }

    class ShutdownTask implements Runnable {

        @Override
        public void run() {
            status.setState(LifeCycleState.shutdown);
            try {
                switch (status.getState()) {
                case running:
                    lifeCycleTaskContext.serviceLifeCycleControl.get().stopServices();
                    break;
                case init:
                case fatal:
                case shutdown:
                }
            } catch (Throwable t1) {
                abortServices();
            } 
        }
    }

    /**
     * This task is created in another thread, so constructor can not access
     * state.
     */
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
                if (throwable instanceof LifeCycleTaskException) {
                    /**
                     * The exception was caused by code other than this class.
                     * Thus ServiceLifeCycleControl or one of the services
                     * during start/stop/abort. There is no recovery possible
                     * from this.
                     */
                    postFatalTask();
                } else {
                    /**
                     * The exception was causes by code in this class. There is
                     * no recovery from this other than restarting the Java
                     * application / WebContainer.
                     */
                    panic();
                }
            } else {
                // Exception in EventBusThread
                switch (status.getState()) {
                case running:
                    // exception during start of services
                    executorServiceExecute(new RestartTask());
                    break;
                case init:
                    // exception during initial start of services means unstable software                    
                case fatal:
                case shutdown:
                default:
                    postFatalTask();
                }
            }
        }
    }

    private void panic() {
        status.setState(LifeCycleState.fatal);
        executorService.shutdownNow();
        abortServices();
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
        executorServiceExecute(new StartupTask());
    }

    private void postFatalTask() {
        executorServiceExecute(new FatalTask());
    }

    private void verifyStateIs(final LifeCycleState requiredState) {
        if (!status.stateEquals(requiredState)) {
            throw new RuntimeException(ERROR_INVALID_STATE);
        }
    }

    @Override
    public void shutdown() {
        executorServiceExecute(new ShutdownTask());
        // The
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
        // Exception has
        final String providedName = ThreadFactoryFactory.getProvidedName(thread);
        executorServiceExecute(new UncaughtExceptionTask(providedName, throwable));
    }

    @Override
    public int getActiveServiceCount() {
        try {
            Future<Optional<ServiceLifeCycleControl>> serviceLifeCycleControlFuture = executorService
                    .submit(new GetServiceLifeCycleControlTask());
            Optional<ServiceLifeCycleControl> serviceLifeCycleControl;
            serviceLifeCycleControl = serviceLifeCycleControlFuture.get();
            return serviceLifeCycleControl.isPresent() ? serviceLifeCycleControl.get().getActiveServiceCount() : 0;
        } catch (InterruptedException | ExecutionException | RejectedExecutionException e) {
            throw new RuntimeException(GET_ACTIVESERVICECOUNT_ERROR, e);
        }
    }

    private void executorServiceExecute(final Runnable runnable) {
        try {
            executorService.execute(runnable);
        } catch (RejectedExecutionException e) {
            // ExecutorService is no longer active, i.e. the underlying thread
            // was stopped. So shutdown was called.
        }
    }

    private void abortServices() {
        try {
            lifeCycleTaskContext.serviceLifeCycleControl.get().abortServices();
        } catch (Throwable t) {
            // log, aborting of of the services failed.
        }
    }
}
