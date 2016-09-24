package org.mahu.virtualfs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;

/**
 * VirtualFileSystem is an in-memory copy of the FileSystem.
 * 
 * It enables fast access.
 * 
 * It is serializable, so it can be saved, restored and transported via HTPP.
 */
public class VirtualFileSystem {

    public final static String FILE_SEPERATOR = "/";
    private final Charset UTF8_CHARSET = Charset.forName("UTF-8");

    private final Map<String, Object> directoriesAndFiles;

    public VirtualFileSystem() {
        directoriesAndFiles = new TreeMap<>();
    }

    public VirtualFileSystem(Map<String, Object> directoriesAndFiles) {
        this.directoriesAndFiles = directoriesAndFiles;
    }

    public static VirtualFileSystem readFromDisk(final String directoryName) throws IOException {
        File directory = new File(directoryName);
        final String relativeParentDirname = "";
        final VirtualFileSystem fs = new VirtualFileSystem();
        fs.addFilesAndDirectoriesRecursive(relativeParentDirname, directory);
        return fs;
    }

    public boolean dirExists(final String dir) {
        final Object value = directoriesAndFiles.get(normalize(dir));
        return value == null && directoriesAndFiles.containsKey(normalize(dir));
    }

    public boolean fileExists(final String file) {
        final Object value = directoriesAndFiles.get(normalize(file));
        return value instanceof byte[];
    }

    public byte[] serialize() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(directoriesAndFiles);
        oos.close();
        return bos.toByteArray();
    }

    public static VirtualFileSystem readFromStream(byte[] content) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(content);
        ObjectInputStream ois = new ObjectInputStream(bis);
        @SuppressWarnings("unchecked")
        Map<String, Object> directoriesAndFiles = (TreeMap<String, Object>) ois.readObject();
        ois.close();
        return new VirtualFileSystem(directoriesAndFiles);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (String dir : directoriesAndFiles.keySet()) {
            sb.append(dir).append("\n");
        }
        return sb.toString();
    }

    private void addFilesAndDirectoriesRecursive(final String relativeParentDirname, final File directory)
            throws IOException {
        // If access is denied to a file or folder a FileNotFoundException is
        // throw.
        final File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                final String relativeDirname = relativeParentDirname + FILE_SEPERATOR + file.getName();
                byte[] value = java.nio.file.Files.readAllBytes(Paths.get(file.getAbsolutePath()));
                directoriesAndFiles.put(relativeDirname, value);
            } else if (file.isDirectory()) {
                final String relativeDirname = relativeParentDirname + FILE_SEPERATOR + file.getName();
                directoriesAndFiles.put(relativeDirname, null);
                addFilesAndDirectoriesRecursive(relativeDirname, file);
            }
        }
    }

    public String getFileAsString(final String file) {
        return new String(getFileAsBytes(normalize(file)), UTF8_CHARSET);
    }

    public byte[] getFileAsBytes(final String file) {
        final Object value = directoriesAndFiles.get(normalize(file));
        if (value instanceof byte[]) {
            return (byte[]) value;
        }
        throw new RuntimeException("File not found, file=" + normalize(file));
    }

    private String normalize(String dorOrFileName) {
        return dorOrFileName.startsWith(FILE_SEPERATOR) ? dorOrFileName : FILE_SEPERATOR + dorOrFileName;
    }

}
