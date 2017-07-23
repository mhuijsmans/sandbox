package org.mahu.javatry;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CallableTest {

    public interface ICallable<V> {
        V call();
    }

    public interface IAreYouReadyCallableNoReturnValue {
        boolean call();
    }

    static class A {

        <T> T execute(String[] s, ICallable<T> c) {
            return c.call();
        }

        void executeMaxTimes(final int maxTimes, IAreYouReadyCallableNoReturnValue c) {
            boolean isReady = false;
            for (int i = 0; i < maxTimes && !isReady; i++) {
                isReady = c.call();
            }
            if (!isReady) {
                throw new RuntimeException();
            }
        }

    }

    @Test
    public void callableWithReturnValue() {
        A a = new A();
        final String hello = "hello";
        String s = a.execute(new String[] { hello }, () -> {
            return hello;
        });

        assertEquals(hello, s);
    }

    @Test
    public void areYouReadyCallable() {
        A a = new A();
        final String hello = "hello";
        a.executeMaxTimes(5, () -> {
            return true;
        });
    }

}
