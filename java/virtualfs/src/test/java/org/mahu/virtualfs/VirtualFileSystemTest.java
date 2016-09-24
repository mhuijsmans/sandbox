package org.mahu.virtualfs;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class VirtualFileSystemTest {

    private static final String DIR1 = "/dir1";
    private static final String DIR2 = "/dir2";
    private static final String DIR3 = "/dir3";
    private static final String DIR21 = "/dir2/dir21";

    private static final String DOC11 = "/dir1/doc11.txt";
    private static final String DOC12 = "/dir1/doc12.txt";
    private static final String DOC211 = "/dir2/dir21/doc211.txt";
    private static final String DOC212 = "/dir2/dir21/doc212.txt";
    private static final String DOC213 = "/dir2/dir21/doc213.txt";

    private static final String DOC11_CONTENT = "doc11";
    private static final String DOC211_CONTENT = "doc211";
    private static final String DOC212_CONTENT = "doc212";

    private static String getTopDir() throws IOException {
        final ClassLoader classLoader = VirtualFileSystemTest.class.getClassLoader();
        java.net.URL urlFile = classLoader.getResource("topdir" + DOC11);
        final File file = new File(urlFile.getFile());
        final String parentDir = file.getParent();
        final String pathTopDir = new File(parentDir, "..").getCanonicalPath();
        return pathTopDir;
    }

    @Test
    public void readFromDisk_correctContent() throws IOException {
        final String pathTopDir = getTopDir();

        VirtualFileSystem fs = VirtualFileSystem.readFromDisk(pathTopDir);

        assertCorrectContent(fs);
    }

    @Test
    public void readFromStream_correctContent() throws IOException, ClassNotFoundException {
        final String pathTopDir = getTopDir();
        VirtualFileSystem temFs = VirtualFileSystem.readFromDisk(pathTopDir);
        byte[] serialized = temFs.serialize();

        VirtualFileSystem fs = VirtualFileSystem.readFromStream(serialized);

        System.out.println(fs);

        assertCorrectContent(fs);
    }

    @Test(expected = RuntimeException.class)
    public void getFileAsString_fileIsADirecory_exception() throws IOException {
        final String pathTopDir = getTopDir();
        VirtualFileSystem fs = VirtualFileSystem.readFromDisk(pathTopDir);

        fs.getFileAsString(DIR1);
    }

    private void assertCorrectContent(VirtualFileSystem fs) {
        assertTrue(fs.dirExists(DIR1));
        assertTrue(fs.dirExists(DIR1.substring(1)));
        assertTrue(fs.dirExists(DIR2));
        assertTrue(fs.dirExists(DIR21));
        assertFalse(fs.dirExists(DIR3));
        assertFalse(fs.dirExists(DOC11));

        assertTrue(fs.fileExists(DOC11));
        assertTrue(fs.fileExists(DOC11.substring(1)));
        assertTrue(fs.fileExists(DOC211));
        assertTrue(fs.fileExists(DOC212));
        assertFalse(fs.fileExists(DOC12));
        assertFalse(fs.fileExists(DOC213));
        assertFalse(fs.fileExists(DIR1));

        assertEquals(DOC11_CONTENT, fs.getFileAsString(DOC11));
        assertEquals(DOC11_CONTENT, fs.getFileAsString(DOC11.substring(1)));
        assertEquals(DOC211_CONTENT, fs.getFileAsString(DOC211));
        assertEquals(DOC212_CONTENT, fs.getFileAsString(DOC212));

        assertArrayEquals(DOC11_CONTENT.getBytes(), fs.getFileAsBytes(DOC11));
        assertArrayEquals(DOC11_CONTENT.getBytes(), fs.getFileAsBytes(DOC11.substring(1)));
        assertArrayEquals(DOC211_CONTENT.getBytes(), fs.getFileAsBytes(DOC211));
        assertArrayEquals(DOC212_CONTENT.getBytes(), fs.getFileAsBytes(DOC212));
    }

}
