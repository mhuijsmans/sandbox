package org.mahu.proto.webguice.worktask;

public class TaskResultData<T> {

    private final Object lock = new Object();
    private T resultData;

    public T get() {
        synchronized (lock) {
            return resultData;
        }
    }

    public void set(T object) {
        synchronized (lock) {
            resultData = object;
        }
    }
}
