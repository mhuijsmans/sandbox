package org.mahu.proto.mymavenplugin.test;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;
import org.mahu.proto.commons.IOUtils;

public class AppTest 
{
    @Test
    public void testMojoBehavior()
    {
		File testCassesDir = IOUtils.getResourceFile(AppTest.class,".");
		File targetDir = testCassesDir.getParentFile();
		assertTrue((new File(targetDir, "touch.txt")).exists());
        assertTrue( true );
    }
}
