package org.mahu.proto.lifecycle.impl;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

class LifeCycleManagerExecutorService {

    static final String THREAD_NAME = "LifeCycleManager.Thread";

    private ExecutorService executorService;

    void init(final UncaughtExceptionHandler uncaughtExceptionHandler) {
        executorService = ThreadFactoryFactory.createSingleThreadExecutor(THREAD_NAME, uncaughtExceptionHandler);
    }

    void asyncExecute(final Runnable runnable) {
        try {
            executorService.execute(runnable);
        } catch (RejectedExecutionException e) {
            // ExecutorService is no longer active, i.e. the underlying thread
            // was stopped. So shutdown was called.
        }
    }

    void shutdown() {
        ExecutorUtils.shutdown(executorService);
    }

    <T> Future<T> submit(Callable<T> task) {
        return executorService.submit(task);
    }

    public void shutdownNow() {
        ExecutorUtils.shutdownNow(executorService);
    }

}
