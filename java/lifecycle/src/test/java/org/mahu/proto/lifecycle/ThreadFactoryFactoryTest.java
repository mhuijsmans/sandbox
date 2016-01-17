package org.mahu.proto.lifecycle;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.mahu.proto.lifecycle.example2.UncaughtExceptionInMemoryLog;
import org.mahu.proto.lifecycle.impl.ThreadFactoryFactory;

public class ThreadFactoryFactoryTest {

    private final UncaughtExceptionInMemoryLog uncaughtExceptionHandler = new UncaughtExceptionInMemoryLog();

    @Test
    public void invalidName_exception() {
        assertInvalidName(null);
        assertInvalidName("");
        assertInvalidName("-a");
        assertInvalidName("a-");
        assertInvalidName("a-b");
    }

    private void assertInvalidName(final String name) {
        try {
            ThreadFactoryFactory.createNamedThreadFactory(name, uncaughtExceptionHandler);
            fail("Exception expected because of invalid name");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

}
