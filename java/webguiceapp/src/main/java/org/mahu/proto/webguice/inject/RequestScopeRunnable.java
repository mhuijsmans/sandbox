package org.mahu.proto.webguice.inject;

// RequestScopeRunnable because Guice uses Callable which throws an
// exception.
public interface RequestScopeRunnable<T> {
    public T run();
}
