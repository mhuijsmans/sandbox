package org.mahu.proto.lifecycle.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.mahu.proto.lifecycle.impl.ReadyAbortLock;
import org.mahu.proto.lifecycle.impl.ReadyAbortLock.LockResult;

public class ReadyAbortLockTest {

    @Rule
    public Timeout globalTimeout = Timeout.seconds(30);

    private ExecutorService threadpool;
    private ReadyAbortLock lock;

    private class AsyncLockWait3 implements Callable<LockResult> {
        final int timeoutInMs;
        final ReadyAbortLock lock = new ReadyAbortLock();
        final CountDownLatch cdl = new CountDownLatch(1);

        AsyncLockWait3(final int timeoutInMs) {
            this.timeoutInMs = timeoutInMs;
        }

        @Override
        public LockResult call() throws InterruptedException {
            synchronized (lock) {
                cdl.countDown();
                return lock.wait3(timeoutInMs);
            }
        }
    }

    @Before
    public void createLock() {
        threadpool = Executors.newFixedThreadPool(2);
        lock = new ReadyAbortLock();
    }

    @After
    public void deletLock() {
        new ReadyAbortLock();
        threadpool.shutdownNow();
        threadpool = null;
    }

    @Test
    public void lock_waitZero_timeout() throws InterruptedException {
        synchronized (lock) {
            assertEquals(LockResult.timeout, lock.wait2(0));
        }
    }

    @Test
    public void wait2_wait100M_timeout() throws InterruptedException {
        final int HUNDRED_MS = 10;
        synchronized (lock) {
            assertEquals(LockResult.timeout, lock.wait2(HUNDRED_MS));
        }
    }

    @Test
    public void lock_waitNegative_exception() throws InterruptedException {
        final int INVALID_TIME = -1;
        try {
            synchronized (lock) {
                lock.wait2(INVALID_TIME);
                fail("Exception expected, because wait < 0");
            }
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    public void lock_TimeOutTooHigh_exception() throws InterruptedException {
        final long INVALID_TIME = Long.MAX_VALUE;
        try {
            synchronized (lock) {
                lock.wait2(INVALID_TIME);
                fail("Exception expected, because wait < 0");
            }
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    public void wait3_wait100M_timeout() {
        final int HUNDRED_MS = 10;
        synchronized (lock) {
            assertEquals(LockResult.timeout, lock.wait3(HUNDRED_MS));
        }
    }

    @Test
    public void ready_noResult_isReadyIsTrue() {
        synchronized (lock) {
            lock.ready();
        }

        assertTrue(lock.isReady());
        assertFalse(lock.isAborted());
    }

    @Test
    public void abort_noResult_isAbortIsTrue() {
        synchronized (lock) {
            lock.abort();
        }

        assertTrue(lock.isAborted());
        assertFalse(lock.isReady());
    }

    @Test
    public void ready_resultIsAbort_isAbortIsTrue() {
        synchronized (lock) {
            lock.abort();

            lock.ready();
        }

        assertFalse(lock.isReady());
        assertTrue(lock.isAborted());
    }

    @Test
    public void await3_interrupted_resultIsInterrupted() throws InterruptedException, ExecutionException {
        final AsyncLockWait3 asyncLockWait3 = new AsyncLockWait3(10000);
        final Future<LockResult> futureWait3 = threadpool.submit(asyncLockWait3);
        asyncLockWait3.cdl.await();
        synchronized(asyncLockWait3.lock) {
            threadpool.shutdownNow();
        }
        assertEquals(LockResult.interrupted, futureWait3.get());
        assertFalse(asyncLockWait3.lock.isAborted());
        assertFalse(asyncLockWait3.lock.isReady());
    }
    
    @Test
    public void await3_ready_isAbortIsTrue() throws InterruptedException, ExecutionException {
        final AsyncLockWait3 asyncLockWait3 = new AsyncLockWait3(10000);
        final Future<LockResult> futureWait3 = threadpool.submit(asyncLockWait3);
        asyncLockWait3.cdl.await();
        synchronized(asyncLockWait3.lock) {
            asyncLockWait3.lock.ready();
        }
        
        assertEquals(LockResult.ready, futureWait3.get());        
        assertTrue(asyncLockWait3.lock.isReady());
        assertFalse(asyncLockWait3.lock.isAborted());
    }
    
    @Test
    public void await3_abort_isAbortIsTrue() throws InterruptedException, ExecutionException {
        final AsyncLockWait3 asyncLockWait3 = new AsyncLockWait3(10000);
        final Future<LockResult> futureWait3 = threadpool.submit(asyncLockWait3);
        asyncLockWait3.cdl.await();
        synchronized(asyncLockWait3.lock) {
            asyncLockWait3.lock.abort();
        }
        
        assertEquals(LockResult.aborted, futureWait3.get());        
        assertTrue(asyncLockWait3.lock.isAborted());
        assertFalse(asyncLockWait3.lock.isReady());
    }

}
