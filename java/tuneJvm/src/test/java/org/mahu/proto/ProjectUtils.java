package org.mahu.proto;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

public final class ProjectUtils {

    /**
     * Return path of the project /target directory. Because the /target directory is project specific the provided
     * input class shall belong to the project of which the /target directory is requested.
     *
     * The use of this method enables the storage or test specific data such that the file system remains clean. A clean
     * will remove any project temporary data.
     *
     * @param cls
     *            for which to return /target folder
     * @return path /target directory
     * @throws UnsupportedEncodingException
     */
    public final static String getTargetDirectory(final Class<?> cls) throws UnsupportedEncodingException {
        final String relPath = getDirectoryOfClass(cls);
        // return a path with a collapsed filename (i.e. without /..).
        return new File(relPath).toPath().resolve("../../target").normalize().toFile().getAbsolutePath();

    }

    public final static String getDirectoryOfClass(final Class<?> cls) {
        try {
            return cls.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        } catch (final URISyntaxException e) {
            throw new RuntimeException("Failed to retrieve path for class " + cls.getName(), e);
        }
    }

    public final static String getDirRelativeToDirOfClass(final Class<?> cls, final String relativeDirectory) {
        final String relPath = ProjectUtils.getDirectoryOfClass(cls);
        final File dir = new File(relPath, relativeDirectory);
        try {
            return dir.getCanonicalPath();
        } catch (final IOException e) {
            return dir.getAbsolutePath();
        }
    }

    private ProjectUtils() {
    }
}