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

    private boolean isReady = false;
    private boolean isAborted = false;

    public void wait2(final long timeoutInMs) throws InterruptedException {
        if (timeoutInMs > 0) {
            final long startTime = System.currentTimeMillis();
            final long endTime = startTime + timeoutInMs;
            if (endTime < startTime) {
                throw new IllegalArgumentException("TimeoutInMs too high=" + timeoutInMs);
            }
            long timeLeftInMs = 1; // value doesn't matter, must be > 0
            while (!isReady && !isAborted && timeLeftInMs > 0) {
                final long currentTime = System.currentTimeMillis();
                timeLeftInMs = Math.max(endTime - currentTime, 0);
                if (timeLeftInMs > 0) {
                    // next will will wait forever when timeLeftInMs == 0
                    wait(timeLeftInMs);
                }
            }
        } else {
            if (timeoutInMs < 0) {
                throw new IllegalArgumentException("Invalid timeoutInMs=" + timeoutInMs);
            }
        }
    }

    public void ready() {
        isReady = true;
        notify();
    }

    public boolean isReady() {
        return isReady;
    }

    public void abort() {
        isAborted = true;
        notify();
    }

    public boolean isAborted() {
        return isAborted;
    }

}
