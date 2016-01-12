package org.mahu.proto.lifecycle.impl;

public class StopEvent {
    private final static long STOP_EVENT_TIMEOUT_IN_MS = 60*1000; 

    private final ReadyAbortLock lock = new ReadyAbortLock();

    public boolean waitForStopped() {
        synchronized (lock) {
            try {
                lock.wait2(STOP_EVENT_TIMEOUT_IN_MS);
            } catch (InterruptedException e) {
                // Some high power started interrupting threads.
            }
            return lock.isReady();
        }

    }

    public void stopped() {
        synchronized (lock) {
            lock.ready();
            lock.notify();
        }
    }

}
