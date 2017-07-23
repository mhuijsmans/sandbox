package org.mahu.javatry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

public class TryCatchFinallyTest {

    public int catchCount = 0;
    public int finallyCount = 0;

    @Test
    public void areYouReadyCallable() {
        try {
            callFunctions();
            fail("functions thows exception");
        } catch (RuntimeException e) {
            // empty
        }

        assertEquals(1, catchCount);
        assertEquals(1, finallyCount);
    }

    private void callFunctions() {
        try {
            throw new RuntimeException();
        } catch (RuntimeException e) {
            catchCount++;
            throw e;
        } finally {
            finallyCount++;
        }
    }

}
