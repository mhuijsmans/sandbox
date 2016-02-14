package org.mahu.proto.lifecycle.impl;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class is the UncaughtExceptionHhandler for the ServicesLifeCycleControl
 * object. It will forward the first exception to the parent
 * UncaughtExceptionHandler.
 */
class ServicesLifeCycleControlUncaughtExceptionHandler implements UncaughtExceptionHandler {

    private final LifeCycleManagerUncaughtExceptionHandler parent;
    private final int id;
    private final AtomicBoolean isExceptionForwarded;

    ServicesLifeCycleControlUncaughtExceptionHandler(final LifeCycleManagerUncaughtExceptionHandler parent, final int id) {
        this.parent = parent;
        this.id = id;
        isExceptionForwarded = new AtomicBoolean(false);
    }

    @Override
    public void uncaughtException(final Thread thread, Throwable throwable) {
        if (isExceptionForwarded.getAndSet(true) == false) {
            parent.forwardedFirstUncaughtException(id, thread, throwable);
        } else {
            parent.forwardNextUncaughtException(id, thread, throwable);
        }
    }

}
