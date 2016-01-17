package org.mahu.proto.lifecycle.example2;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Optional;

import org.mahu.proto.lifecycle.impl.ReadyAbortLock;

public class UncaughtExceptionInMemoryLog implements UncaughtExceptionHandler {

    private int exceptionCounter = 0;
    private Optional<Throwable> lastThrownException = Optional.empty();
    private Optional<Thread> threadLastThrownException = Optional.empty();
    private ReadyAbortLock lock = new ReadyAbortLock();
  
    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        synchronized (lock) {
            exceptionCounter++;
            lastThrownException = Optional.ofNullable(throwable);
            threadLastThrownException= Optional.ofNullable(thread);
            lock.ready();
        }
    }
    
    public int getExceptionCount() {
        synchronized (lock) {
            return exceptionCounter;
        }
    }

    public void waitForExceptionCaught(final int timeoutInMs) {
        synchronized (lock) {
            lock.wait3(timeoutInMs);
        }
    }

    public Optional<Throwable> getLastThrownException() {
        synchronized (lock) {
            return lastThrownException;
        }
    }
    
    public Optional<Thread> getThreadLastThrownException() {
        synchronized (lock) {
            return threadLastThrownException;
        }
    }    
}
