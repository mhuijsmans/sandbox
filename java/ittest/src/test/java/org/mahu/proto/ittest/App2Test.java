package org.mahu.proto.ittest;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

// ifinitest doesn't run this test, because of infinitest.filters

public class App2Test {

    @Test
    public void testApp() {
        App.main(new String[] {"ittest"});
        assertTrue(true);
    }
}
