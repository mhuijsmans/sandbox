package org.mahu.proto.fileioperformance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import org.junit.Test;
import org.mahu.proto.commons.Chrono;
import org.mahu.proto.commons.IOUtils;

public class AppTest {

	public final static boolean DEBUG = false;
	public final static String FILENAME = "test.txt";
	public final static String TEXT = "01234567890123456789012345678901234567890123456789";

	@Test
	public void testDifferentImplementations() throws IOException {
		File file = createTestFile(FILENAME, TEXT + TEXT + "\n", 1000 * 300);
		System.out.println("Filesize " + file.length());
		Chrono chrono = new Chrono();

		for (int i = 0; i < 5; i++) {

			testWithoutNioReadLines(file);
			printElapsedTimeAndResetChrono(chrono, "testWithoutNioReadLines");

			testWithoutNioReadAllAtOnce(file);
			printElapsedTimeAndResetChrono(chrono,
					"testWithoutNioReadAllAtOnce");

			testRandomAccessWithDirectNio(file);
			printElapsedTimeAndResetChrono(chrono, "testRandomAccessWithDirectNio");

			testReadFileWithFixedSizeNioBuffer(file);
			printElapsedTimeAndResetChrono(chrono,
					"testReadFileWithFixedSizeNioBuffer");

			testReadFileWithNioMappedByteBuffer(file);
			printElapsedTimeAndResetChrono(chrono,
					"testReadFileWithNioMappedByteBuffer");
		}
	}

	protected void printElapsedTimeAndResetChrono(final Chrono chrono,
			final String who) {
		long elapsed = chrono.elapsedMs();
		System.out.println(who + ", elapsed: " + elapsed);
		chrono.reset();
	}

	public void testWithoutNioReadLines(final File file) {
		BufferedReader br = null;
		String sCurrentLine = null;
		try {
			br = new BufferedReader(new FileReader(file));
			while ((sCurrentLine = br.readLine()) != null) {
				if (DEBUG) {
					System.out.println(sCurrentLine);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public void testWithoutNioReadAllAtOnce(final File file) throws IOException {
		final byte[] b = IOUtils.readAllBytes(file);
		int cnt = 0;
		for (int i = 0; i < b.length; i++) {
			cnt += b[i];
		}
	}

	public void testRandomAccessWithDirectNio(final File file) {
		try {
			RandomAccessFile aFile = new RandomAccessFile(file, "r");
			FileChannel inChannel = aFile.getChannel();
			long fileSize = inChannel.size();
			ByteBuffer buffer = ByteBuffer.allocateDirect((int) fileSize);
			inChannel.read(buffer);
			System.out
					.println("testRandomAccessWithDirectNio, isDirect   "
							+ buffer.isDirect() + ", isReadonly "
							+ buffer.isReadOnly());
			buffer.rewind();
			buffer.flip();
			readCharacters(buffer);
			inChannel.close();
			aFile.close();
		} catch (IOException exc) {
			System.out.println(exc);
			System.exit(1);
		}
	}

	public void testReadFileWithFixedSizeNioBuffer(final File file)
			throws IOException {
		RandomAccessFile aFile = new RandomAccessFile(file, "r");
		FileChannel inChannel = aFile.getChannel();
		ByteBuffer buffer = ByteBuffer.allocateDirect(4096);
		while (inChannel.read(buffer) > 0) {
			buffer.flip();
			readCharacters(buffer);
			buffer.clear(); // do something with the data and clear/compact it.
		}
		inChannel.close();
		aFile.close();
	}

	public void testReadFileWithNioMappedByteBuffer(final File file)
			throws IOException {
		RandomAccessFile aFile = new RandomAccessFile(file, "r");
		FileChannel inChannel = aFile.getChannel();
		MappedByteBuffer buffer = inChannel.map(FileChannel.MapMode.READ_ONLY,
				0, inChannel.size());
		buffer.load();
		readCharacters(buffer);
		System.out.println("testReadFileWithNioMappedByteBuffer, isDirect   "
				+ buffer.isDirect() + ", isLoaded   " + buffer.isLoaded()
				+ ", isReadonly " + buffer.isReadOnly());
		buffer.clear(); // do something with the data and clear/compact it.
		inChannel.close();
		aFile.close();

	}

	protected void readCharacters(final ByteBuffer buffer) {
		int cnt = 0;
		if (DEBUG) {
			for (int i = 0; i < buffer.limit(); i++) {
				char c = (char) buffer.get();
				System.out.print(c);
			}
		} else {
			for (int i = 0; i < buffer.limit(); i++) {
				cnt += buffer.get();
			}

		}
	}

	private File createTestFile(final String name, final String text,
			final int nrOfStrings) throws IOException {
		File testClassesDir = IOUtils.getResourceFile(AppTest.class, ".");
		File targetDir = testClassesDir.getParentFile();
		File testFile = new File(targetDir, name);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < nrOfStrings; i++) {
			sb.append(text);
		}
		IOUtils.writeBytes(testFile, sb.toString().getBytes());
		return testFile;

	}

}
