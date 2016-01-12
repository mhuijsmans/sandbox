package org.mahu.proto.lifecycle;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.mahu.proto.lifecycle.impl.ReadyAbortLock;

public class ReadyAbortLockTest {

    @Rule
    public Timeout globalTimeout = Timeout.seconds(30);

    @Test
    public void lock_waitZero_ok() throws InterruptedException {
        ReadyAbortLock lock = new ReadyAbortLock();

        synchronized (lock) {
            lock.wait2(0);
        }
    }

    @Test
    public void lock_wait100M_ok() throws InterruptedException {
        ReadyAbortLock lock = new ReadyAbortLock();

        final int HUNDRED_MS = 100;
        synchronized (lock) {
            lock.wait2(HUNDRED_MS);
        }
    }

    @Test
    public void lock_waitNegative_exceptionk() throws InterruptedException {
        ReadyAbortLock lock = new ReadyAbortLock();

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
    public void lock_TimeOutTooHigh_exceptionk() throws InterruptedException {
        ReadyAbortLock lock = new ReadyAbortLock();

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

}
