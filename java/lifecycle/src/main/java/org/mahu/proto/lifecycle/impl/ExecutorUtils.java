package org.mahu.proto.lifecycle.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class ExecutorUtils {
    
    public static final String ERROR_AT_SHUTDOWN_TASKS_STILL_RUNNING = "Shutdown of executor service failed";
    private static final int TASKSCOMPLETED_TIMEOUT_IN_MS = 30000;
    
    public static void shutdown( final ExecutorService executorService) {
        shutdown(executorService, TASKSCOMPLETED_TIMEOUT_IN_MS);
    }
    
    public static void shutdown( final ExecutorService executorService, final int timeoutInMs) {
        executorService.shutdown();
        try {
            if (!(executorService.awaitTermination(timeoutInMs, TimeUnit.MILLISECONDS))) {
                executorService.shutdownNow();
                if (!(executorService.awaitTermination(timeoutInMs, TimeUnit.MILLISECONDS))) {
                   throw new RuntimeException(ERROR_AT_SHUTDOWN_TASKS_STILL_RUNNING); 
                }
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

}
