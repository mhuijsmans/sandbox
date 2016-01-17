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

public class ExecutorUtilsTest {

    private final static int TESTCASE_TIMEOUT_IN_MS = 30 * 1000;
    private final static String THREADNAME = "ExecutorUtilsTest.TheadName";

    boolean checkNoException;
    UncaughtExceptionInMemoryLog uncaughtHandler;
    ExecutorService executorService;

    @Rule
    public Timeout globalTimeout = Timeout.seconds(30);

    static class BlockingTask implements Runnable {
        private final CountDownLatch cdl1 = new CountDownLatch(1);
        private final CountDownLatch cdl2 = new CountDownLatch(1);

        @Override
        public void run() {
            cdl1.countDown();
            try {
                cdl2.await();
            } catch (InterruptedException e) {
            }
        }

        public void waitUntilRunIsCalled() throws InterruptedException {
            cdl1.await();
        }

        public void continueExecution() {
            cdl2.countDown();
        }

    }

    static class Task implements Runnable {
        private final CountDownLatch cdl1 = new CountDownLatch(1);

        @Override
        public void run() {
            cdl1.countDown();
        }

        public boolean isExecuted() throws InterruptedException {
            return cdl1.getCount() == 0;
        }

    }

    static class ExceptionTask implements Runnable {

        @Override
        public void run() {
            throw new RuntimeException();
        }

    }

    @Before
    public void createExecutorService() {
        checkNoException = true;
        uncaughtHandler = new UncaughtExceptionInMemoryLog();
        executorService = ThreadFactoryFactory.createSingleThreadExecutor(THREADNAME, uncaughtHandler);
    }

    @After
    public void deleteExecutorService() throws InterruptedException {
        if (checkNoException) {
            assertEquals(0, uncaughtHandler.getExceptionCount());
        }
        executorService.shutdown();
        assertTrue(executorService.awaitTermination(30, TimeUnit.SECONDS));
        uncaughtHandler = null;
        executorService = null;
    }

    @Test
    public void submitTask_beforeShutDown_taskIsExecuted() throws InterruptedException {
        BlockingTask run1 = new BlockingTask();
        executorService.submit(run1);
        run1.waitUntilRunIsCalled();

        Task task = new Task();
        executorService.submit(task);

        executorService.shutdown();
        run1.continueExecution();

        assertTrue(executorService.awaitTermination(30, TimeUnit.SECONDS));
        assertTrue(task.isExecuted());
    }

    @Test
    public void submitTask_afterShutDownTaskActive_rejected() throws InterruptedException {
        BlockingTask run1 = new BlockingTask();
        executorService.submit(run1);
        run1.waitUntilRunIsCalled();

        executorService.shutdown();

        try {
            executorService.submit(new Task());
            fail("Posting on shutdown queue shall result in exception");
        } catch (RejectedExecutionException e) {
            assertTrue(true);
        }
        run1.continueExecution();
    }

    @Test
    public void submitTask_afterShutDownNoTaskActive_rejected() throws InterruptedException {
        executorService.shutdown();

        try {
            executorService.submit(new Task());
            fail("Posting on shutdown queue shall result in exception");
        } catch (RejectedExecutionException e) {
            assertTrue(true);
        }
    }

    @Test
    public void submit_ExceptionTask_exceptionCaught() throws InterruptedException {
        ExceptionTask task = new ExceptionTask();
        executorService.execute(task);
        TestUtils.pollingWait(() -> uncaughtHandler.getExceptionCount() != 0, TESTCASE_TIMEOUT_IN_MS);

        assertEquals(1, uncaughtHandler.getExceptionCount());
        assertTrue(uncaughtHandler.getLastThrownException().isPresent());
        assertTrue(uncaughtHandler.getThreadLastThrownException().isPresent());
        final String providedThreadName = ThreadFactoryFactory.getProvidedName(uncaughtHandler.getThreadLastThrownException().get());
        assertEquals(THREADNAME, providedThreadName);
        checkNoException = false;
    }

}
