package org.mahu.proto.lifecycle.impl;

import org.mahu.proto.lifecycle.ILifeCycleManager.LifeCycleState;

/**
 * This task is created in another thread, so constructor can not access state.
 */
class UncaughtExceptionTask extends LifeCycleTask implements Runnable {

    private final String providedThreadName;
    private final Throwable throwable;

    public UncaughtExceptionTask(final LifeCycleTaskContext context, String providedName, Throwable throwable) {
        super(context);
        this.providedThreadName = providedName;
        this.throwable = throwable;
    }

    @Override
    public void run() {
        if (providedThreadName.equals(LifeCycleManagerExecutorService.THREAD_NAME)) {
            if (throwable instanceof LifeCycleTaskException) {
                /**
                 * The exception was caused by code other than this class.
                 * ServiceLifeCycleControl or one of the services has throw this
                 * exception during start/stop/abort. There is no recovery
                 * possible from this.
                 */
                postFatalTask();
            } else {
                /**
                 * The exception was causes by code in this class. So the code
                 * of this class is unstable. So kill everything. There is no
                 * recovery from this other than restarting the Java application
                 * / WebContainer.
                 */
                panic();
            }
        } else {
            // Exception in one of the EventBusThreads.
            switch (getState()) {
            case running:
                // This is expected to be the typical case:
                // an uncaught exception in one of the services after being
                // started by ServiceLifeCycleControl.
                postServicesRestartTask();
                break;
            case init:
                // exception during initial start of services means unstable software.
                // That is detected and handled as LifeCycleTaskException. So exception
                // in init is not possible.
            case fatal:
            case shutdown:
            default:
            }
        }
    }

    /**
     * An fatal error is detected in the code of this class. The only recovery
     * possible is to fix the bug and deployed a new version of this class.
     */
    private void panic() {
        setState(LifeCycleState.fatal);
        abortServicesCatchException();
        shutdownNowExecutorService();
    }

    protected void postServicesRestartTask() {
        asyncExecute(new ServicesRestartTask(getContext()));
    }

    private void postFatalTask() {
        asyncExecute(new FatalTask(getContext()));
    }

}
