package org.mahu.proto.lifecycle.impl;

import java.lang.reflect.Method;
import java.util.Optional;

public class RequestProxyEvent {

    public final static String REQUEST_EXECUTE_ABORT_REASON = "Request execution was aborted";
    public final static String REQUEST_EXECUTE_TIMEOUT_REASON = "Request execution response timout";

    private final static long REQUEST_EXECUTEWAIT_TIMEOUT_IN_MS = 30 * 1000;
    private final static Object NULL = new Object();

    private final ReadyAbortLock lock = new ReadyAbortLock();
    private Optional<Object> result;
    private Optional<Throwable> exception;
    private final Object proxyToObject;
    private final Method method;
    private final Object[] args;

    RequestProxyEvent(final Object proxy, final Method method, final Object[] args) {
        this.proxyToObject = proxy;
        this.method = method;
        this.args = args;
        exception = Optional.empty();
        result = Optional.empty();
    }

    public void execute() {
        try {
            // In case of a void method, the invokeResult is null;
            final Object invokeResult = method.invoke(proxyToObject, args);
            result = Optional.of(invokeResult == null ? NULL : invokeResult);
        } catch (Throwable t) {
            exception = Optional.of(t);
        } finally {
            synchronized (lock) {
                lock.ready();
                lock.notify();
            }
        }
    }

    public Object getResult() throws Throwable {
        waitForExecuted();
        if (exception.isPresent()) {
            throw exception.get();
        }
        if (result.isPresent()) {
            final Object invokeResult = result.get();
            return invokeResult == NULL ? null : invokeResult;
        }
        throw new RuntimeException(lock.isAborted() ? REQUEST_EXECUTE_ABORT_REASON : REQUEST_EXECUTE_TIMEOUT_REASON);
    }

    private boolean waitForExecuted() {
        synchronized (lock) {
            try {
                lock.wait2(REQUEST_EXECUTEWAIT_TIMEOUT_IN_MS);
            } catch (InterruptedException e) {
                // Some high power interrupting threads.
            }
            return lock.isReady();
        }
    }

    public void abort() {
        synchronized (lock) {
            lock.abort();
        }
    }
}
