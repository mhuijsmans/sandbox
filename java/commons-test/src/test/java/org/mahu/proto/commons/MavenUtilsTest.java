package org.mahu.proto.commons;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

public class MavenUtilsTest 
{
    @Test
    public void testApp()
    {
    	MavenUtils utils = new MavenUtils(this);
        assertTrue( utils.getTargetDir().getAbsolutePath().endsWith(File.separator+"target") );
    }
}
