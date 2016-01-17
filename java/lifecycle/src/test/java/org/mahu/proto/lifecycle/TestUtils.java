package org.mahu.proto.lifecycle;

import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

public class TestUtils {
    
    public static void pollingWait(final BooleanSupplier object, final int timeoutInMs) {
        if (timeoutInMs > 0) {
            final long startTime = System.currentTimeMillis();
            final long endTime = System.currentTimeMillis() + timeoutInMs;
            if (endTime < startTime) {
                throw new IllegalArgumentException("TimeoutInMs too high=" + timeoutInMs);
            }
            while (!object.getAsBoolean() && System.currentTimeMillis() < endTime) {
                try {
                    TimeUnit.MILLISECONDS.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

}
