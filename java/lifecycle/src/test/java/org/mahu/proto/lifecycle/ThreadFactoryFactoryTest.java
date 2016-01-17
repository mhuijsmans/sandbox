package org.mahu.proto.lifecycle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.concurrent.ThreadFactory;

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
    
    @Test
    public void threadFactoryName_incrementedForEachCreatedThread() {
        final String name = "mies";
        final ThreadFactory tf= ThreadFactoryFactory.createNamedThreadFactory(name, uncaughtExceptionHandler);
        final Thread t1 = tf.newThread(() -> {} );
        final Thread t2 = tf.newThread(() -> {} );
        
        assertEquals(ThreadFactoryFactory.THREADNAME_PREFIX+"-"+name+"-0", t1.getName());
        assertEquals(ThreadFactoryFactory.THREADNAME_PREFIX+"-"+name+"-1", t2.getName());
    }
    
    @Test
    public void threadFactoryName_correctFormat() {
        final String name = "mies";
        final ThreadFactory tf= ThreadFactoryFactory.createNamedThreadFactory(name, uncaughtExceptionHandler);
        final Thread t1 = tf.newThread(() -> {} );
        final Thread t2 = tf.newThread(() -> {} );
        
        assertEquals(name, ThreadFactoryFactory.getProvidedName(t1));
        assertEquals(name, ThreadFactoryFactory.getProvidedName(t2));
        assertEquals("0", ThreadFactoryFactory.getSequenceNr(t1));
        assertEquals("1", ThreadFactoryFactory.getSequenceNr(t2));
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
