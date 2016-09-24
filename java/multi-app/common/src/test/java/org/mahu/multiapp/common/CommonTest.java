package org.mahu.multiapp.common;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.mahu.multiapp.testcommon.TestCommon;

/**
 * Unit test for simple App.
 */
public class CommonTest
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public CommonTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( CommonTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        (new Common()).hi();
        (new TestCommon()).hi();
    }
}
