package org.mahu.proto.lifecycle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.mahu.proto.lifecycle.example2.UncaughtExceptionInMemoryLog;
import org.mahu.proto.lifecycle.impl.ThreadFactoryFactory;

public class ExecutorServiceTest {
    
    private final static int TESTCASE_TIMEOUT_IN_MS = 30 * 1000;
    private final static String THREADNAME = "ExecutorServiceTest.TheadName";

    @Rule
    public Timeout globalTimeout = Timeout.seconds(30);

    UncaughtExceptionInMemoryLog uncaughtHandler;
    ExecutorService executorService;

    static class ShutdownNowTask implements Runnable {
        private final ExecutorService executorService;

        ShutdownNowTask(final ExecutorService executorService) {
            this.executorService = executorService;
        }

        @Override
        public void run() {
            executorService.shutdownNow();
        }

    }
    
    static class WaitForExternalTriggerToCompleteTask implements Runnable {
        final CountDownLatch started = new CountDownLatch(1);
        final CountDownLatch complete = new CountDownLatch(1);

        @Override
        public void run() {
            started.countDown();
            try {
                complete.await();
            } catch (InterruptedException e) {
                // ignore. test case shall check post conditions
            }
        }

    }

    @Before
    public void createExecutorService() {
        uncaughtHandler = new UncaughtExceptionInMemoryLog();
        executorService = ThreadFactoryFactory.createSingleThreadExecutor(THREADNAME, uncaughtHandler);
    }

    @After
    public void deleteExecutorService() throws InterruptedException {
        assertEquals(0, uncaughtHandler.getExceptionCount());
        executorService.shutdown();
        assertTrue(executorService.awaitTermination(TESTCASE_TIMEOUT_IN_MS, TimeUnit.MILLISECONDS));
        uncaughtHandler = null;
        executorService = null;
    }

    /**
     * This test case verifies that a task that is executed by an
     * executorService can call shutdownNow()
     */
    @Test
    public void submit_taskThatCallShutdownNow_isAllowed() {
        ShutdownNowTask task = new ShutdownNowTask(executorService);
        executorService.execute(task);

        TestUtils.pollingWait(() -> executorService.isShutdown(), TESTCASE_TIMEOUT_IN_MS);
        TestUtils.pollingWait(() -> executorService.isTerminated(), TESTCASE_TIMEOUT_IN_MS);
        assertTrue(executorService.isShutdown());
        assertTrue(executorService.isTerminated());
    }

    @Test    
    public void shutdown_calledTwice_isAllowed() {
        executorService.shutdown();
        executorService.shutdown();
    }
    
    @Test    
    public void shutdownNow_calledTwice_isAllowed() {
        executorService.shutdownNow();
        executorService.shutdownNow();
    }
    
    @Test    
    public void shutdownNow_afterShutDown_isAllowed() {
        executorService.shutdown();
        executorService.shutdownNow();
    }    

    @Test    
    public void shutdown_AfterShutdowNow_isAllowed() {
        executorService.shutdownNow();
        executorService.shutdown();
    }
    
    @Test
    public void execute_afterShutdown_notAccepted() throws InterruptedException {
        WaitForExternalTriggerToCompleteTask task = new WaitForExternalTriggerToCompleteTask();
        executorService.execute(task);
        task.started.await();
        
        executorService.shutdown();
        try {
            // Request execution of a dummy task.
            executorService.execute(() -> {});
            fail("Calling execute after shutdown has been called is not allowed");
        } catch (RejectedExecutionException e) {
            // This is expected behavior
        } finally {
            task.complete.countDown();
        }
        
        assertEquals(0,  task.complete.getCount());
    }
}
