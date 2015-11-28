package org.mahu.proto.ftp;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class IOUtil {

	private final static Logger LOGGER = Logger.getLogger(IOUtil.class
			.getName());

	public static class FileUtilException extends Exception {

		private static final long serialVersionUID = -4116401814520551549L;

		public FileUtilException(final String text) {
			super(text);
		}

	}

	public static void createDir(final String dir) throws FileUtilException {
		checkNotNull(dir);
		File f = new File(dir);
		String fullPath = f.getAbsolutePath();
		if (!f.exists()) {
			if (!f.mkdirs()) {
				throw new FileUtilException("Failed to create directory: "
						+ fullPath);
			}
		} else {
			if (f.isFile()) {
				throw new FileUtilException("Directory exists and is a file: "
						+ fullPath);
			}
		}
	}

	public static boolean fileExists(final String dir, final String file) {
		checkNotNull(dir);
		checkNotNull(file);
		return fileExists(new File(dir, file));
	}

	public static boolean fileExists(final File dir, final String file) {
		checkNotNull(dir);
		checkNotNull(file);
		return fileExists(new File(dir, file));
	}

	public static boolean fileExists(final File file) {
		checkNotNull(file);
		return file.exists() && file.isFile();
	}

	public static void unzip(final File srcZipFile, final File destDir) {
		checkNotNull(srcZipFile);
		checkNotNull(destDir);
		// create output directory if it doesn't exist
		checkArgument(destDir.exists() && !destDir.isFile());
		//
		FileInputStream fis = null;
		// buffer for read and write data to file
		byte[] buffer = new byte[32 * 1024];
		try {
			fis = new FileInputStream(srcZipFile);
			ZipInputStream zis = new ZipInputStream(fis);
			ZipEntry ze = zis.getNextEntry();
			while (ze != null) {
				String fileName = ze.getName();
				File newFile = new File(destDir, fileName);
				if (ze.isDirectory()) {
					LOGGER.info("Creating dir " + newFile.getAbsolutePath());
					newFile.mkdirs();
				} else {
					LOGGER.info("Unzipping to " + newFile.getAbsolutePath());
					// create directories for sub directories in zip
					new File(newFile.getParent()).mkdirs();
					//
					FileOutputStream fos = new FileOutputStream(newFile);
					int len;
					while ((len = zis.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}
					fos.close();
					// close this ZipEntry
					zis.closeEntry();
				}
				ze = zis.getNextEntry();
			}
			// close last ZipEntry
			zis.closeEntry();
			zis.close();
			fis.close();
			fis = null;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeStream(fis);
		}

	}

	public static InputStream getInputStreamFromResource(final Class cls,
			final String resourcePath) {
		LOGGER.info("Accessing resource: " + resourcePath);
		URL url = cls.getClassLoader().getResource(resourcePath);
		if (url == null) {
			return null;
		}
		InputStream is = null;
		try {
			is = url.openStream();
			return is;
		} catch (IOException e) {
			return null;
		}
	}

	public static void closeStream(InputStream is) {
		if (is != null) {
			try {
				is.close();
			} catch (IOException e) {
				// todo: investigate what can be cause and if warning is needed
				LOGGER.warning("IOException when closing stream");
				e.printStackTrace();
			}
		}
	}

}
