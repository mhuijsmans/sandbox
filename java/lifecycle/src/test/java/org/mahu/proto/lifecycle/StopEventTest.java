package org.mahu.proto.lifecycle;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.mahu.proto.lifecycle.impl.StopEvent;

public class StopEventTest {

    @Test
    public void waitForStopped_stopNotCalled_returnFalse() {
        final int TIMEOUT_IN_MS = 10;
        StopEvent stopEvent = new StopEvent(TIMEOUT_IN_MS);

        assertFalse(stopEvent.waitForStopped());
    }

    @Test
    public void waitForStopped_stopCalled_returnFalse() {
        final int TIMEOUT_IN_MS = 10;
        StopEvent stopEvent = new StopEvent(TIMEOUT_IN_MS);
        stopEvent.stopped();

        assertTrue(stopEvent.waitForStopped());
    }

}
