package org.mahu.proto.ittest2.unittest;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

// ifinitest doesn't run this test, because of infinitest.filters

public class UnitTest {

    @Test
    public void testApp() {
        App.main(new String[] {"UnitTest"});
        assertTrue(true);
    }
}
