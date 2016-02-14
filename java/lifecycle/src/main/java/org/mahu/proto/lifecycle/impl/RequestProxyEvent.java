package org.mahu.proto.lifecycle.impl;

import java.lang.reflect.Method;
import java.util.Optional;

import org.mahu.proto.lifecycle.impl.ReadyAbortLock.LockResult;

public class RequestProxyEvent {

    public final static String REQUEST_EXECUTE_ABORT_REASON = "Request execution was aborted";
    public final static String REQUEST_EXECUTE_TIMEOUT_REASON = "Request execution response timout or interrupt";

    private final static long REQUEST_EXECUTEWAIT_TIMEOUT_IN_MS = 30 * 1000;

    private final ReadyAbortLock lock;
    private final Object proxyToObject;
    private final Method method;
    private final Object[] args;
    private Optional<Throwable> exception;
    private Object result;

    RequestProxyEvent(final Object proxy, final Method method, final Object[] args) {
        lock = new ReadyAbortLock();
        proxyToObject = proxy;
        this.method = method;
        this.args = args;
        exception = Optional.empty();
        result = null;
    }

    public void execute() {
        try {
            // In case of a void method that is invoked, the invokeResult is null;
            result = method.invoke(proxyToObject, args);
        } catch (Throwable t) {
            exception = Optional.of(t);
        } finally {
            synchronized (lock) {
                lock.ready();
            }
        }
    }

    public Object getResult() throws Throwable {
        if (waitForExecuted()) {
            if (exception.isPresent()) {
                throw exception.get();
            }
            return result;
        }
        throw new RuntimeException(lock.isAborted() ? REQUEST_EXECUTE_ABORT_REASON : REQUEST_EXECUTE_TIMEOUT_REASON);
    }

    private boolean waitForExecuted() {
        synchronized (lock) {
            return lock.wait3(REQUEST_EXECUTEWAIT_TIMEOUT_IN_MS) == LockResult.ready;
        }
    }

    public void abort() {
        synchronized (lock) {
            lock.abort();
        }
    }
}
