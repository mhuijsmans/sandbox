package org.mahu.proto.lifecycle.impl;

//@formatter:off
/**
 * This class contains a wait2() method that wait according to the JDK
 * recommended wait pattern. This pattern takes into account that thread can
 * wake up spontaneous.
 * 
 * The non-waiting class can (after using synchronized) use ready() or abort() to
 * signal the new state to the waiting class.
 * 
 * This class is used in a multi-threaded situation. 
 * - one thread has calls wait2(..)
 * - a second thread calls ready()
 * - a third thread calls abort();
 * All calling thread shall call the methods in this class within a synchronized block.
 */
//@formatter:on
public class ReadyAbortLock {

    public static enum LockResult {
        unknown, ready, aborted, timeout, interrupted
    }

    private LockResult result = LockResult.unknown;

    public LockResult wait2(final long timeoutInMs) throws InterruptedException {
        if (timeoutInMs > 0) {
            final long startTime = System.currentTimeMillis();
            final long endTime = startTime + timeoutInMs;
            if (endTime < startTime) {
                throw new IllegalArgumentException("TimeoutInMs too high=" + timeoutInMs);
            }
            long timeLeftInMs = 1; // value doesn't matter, must be > 0
            while (result == LockResult.unknown && timeLeftInMs > 0) {
                final long currentTime = System.currentTimeMillis();
                timeLeftInMs = Math.max(endTime - currentTime, 0);
                if (timeLeftInMs > 0) {
                    // next will will wait forever when timeLeftInMs == 0
                    wait(timeLeftInMs);
                }
            }
            if (result == LockResult.unknown) {
                setResult(LockResult.timeout);
            }
        } else {
            if (timeoutInMs < 0) {
                throw new IllegalArgumentException("Invalid timeoutInMs=" + timeoutInMs);
            }
            setResult(LockResult.timeout);
        }
        return result;
    }

    public LockResult wait3(final long timeoutInMs) {
        try {
            wait2(timeoutInMs);
        } catch (InterruptedException e) {
            setResult(LockResult.interrupted);
        }
        return result;
    }

    public void ready() {
        setResult(LockResult.ready);
    }

    public void abort() {
        setResult(LockResult.aborted);
    }

    public boolean isReady() {
        return result == LockResult.ready;
    }

    public boolean isAborted() {
        return result == LockResult.aborted;
    }

    private void setResult(final LockResult newResult) {
        if (result == LockResult.unknown) {
            result = newResult;
            notifyAll();
        }
    }
}
