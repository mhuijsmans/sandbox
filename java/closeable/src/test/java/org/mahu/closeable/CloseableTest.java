package org.mahu.closeable;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

public class CloseableTest {
    
    @Test
    public void testCloseable() throws IOException {
        TestBoolean bool = new TestBoolean();
        try (TestCloseable tc = new TestCloseable(bool)) {
            tc.bla();
        }
        assertTrue(bool.getValue());
    }
    
    @Test
    public void testAutoCloseable() {
        TestBoolean bool = new TestBoolean();
        try (TestAutoCloseable tc = new TestAutoCloseable(bool)) {
            tc.bla();
        }
        assertTrue(bool.getValue());
    }
}
