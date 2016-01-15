package org.mahu.proto.lifecycle.impl;

import org.mahu.proto.lifecycle.impl.ReadyAbortLock.LockResult;

public class StopEvent {
    private final static int STOP_EVENT_TIMEOUT_IN_MS = 60*1000; 

    private final ReadyAbortLock lock = new ReadyAbortLock();
    private final int timeoutInMs;
    
    public StopEvent() {
        this(STOP_EVENT_TIMEOUT_IN_MS);
    }
    
    public StopEvent(final int timeoutInMs) {
        this.timeoutInMs = timeoutInMs;
    }
    
    public boolean waitForStopped() {
        synchronized (lock) {
            return lock.wait3(timeoutInMs) == LockResult.ready;
        }
    }

    public void stopped() {
        synchronized (lock) {
            lock.ready();
        }
    }

}
