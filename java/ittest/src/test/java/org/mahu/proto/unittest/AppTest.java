package org.mahu.proto.unittest;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.mahu.proto.ittest.App;

// ifinitest runs this test (i.e. not included in infinitest.filters)

public class AppTest {

    @Test
    public void testApp() {
        App.main(new String[] {"unittest"});
        assertTrue(true);
    }
}
