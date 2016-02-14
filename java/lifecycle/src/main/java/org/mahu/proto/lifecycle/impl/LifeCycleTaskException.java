package org.mahu.proto.lifecycle.impl;

final class LifeCycleTaskException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final Class<? extends LifeCycleTask> cls;

    LifeCycleTaskException(final Throwable t, final Class<? extends LifeCycleTask> cls) {
        super(t);
        this.cls = cls;
    }

    Class<? extends LifeCycleTask> getTaskClass() {
        return cls;
    }
}
