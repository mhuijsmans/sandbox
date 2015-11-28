package org.mahu.proto.commons;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestUtils {
	
	public static void assertBytesEquals(final byte[] expected,
			final byte[] found) {
		assertBytesEquals(expected, found,0);
	}
	
	public static void assertBytesEquals(final byte[] expected,
			final byte[] found, final int nrOfBytesToPrint) {
		if (expected==null && expected==null) {
			return;
		}
		assertTrue(expected != null);
		assertTrue(found != null);
		assertTrue(expected.length == found.length);
		if (nrOfBytesToPrint>0) {
			print("Expected Bytes", expected, nrOfBytesToPrint);
			print("Found Bytes", found, nrOfBytesToPrint);
		}
		for (int i = 0; i < expected.length; i++) {
			assertEquals("Index: " + i, expected[i], found[i]);
		}
	}	

	public static void print(String msg, byte[] bytes, int nrOfBytesToPrint) {
		System.out.println(msg);
		System.out.println(bytesToString(bytes,
				bytes.length < nrOfBytesToPrint ? bytes.length : nrOfBytesToPrint));
	}

	public static void printPixelData(String string, int nrOfRows,
			int nrOfColumns, byte[] pixelData1) {
		System.out.println(string + nrOfRows + " * " + nrOfColumns);
		int bytesPerPixel = pixelData1.length / (nrOfRows * nrOfColumns);
		int bytesPerRow = nrOfColumns * bytesPerPixel;
		for (int i = 0; i < nrOfRows; i++) {
			System.out.println(bytesToString(pixelData1, i * bytesPerRow,
					bytesPerRow));
		}
	}

	private static String bytesToString(final byte[] bytes, int max) {
		StringBuffer result = new StringBuffer();
		int i = 0;
		while (max > 0) {
			int nrOfBytesToPrint = calculateNrOfBytesToPrint(max);
			result.append(bytesToString(bytes, i, nrOfBytesToPrint));
			result.append("\n");
			max -= nrOfBytesToPrint;
			i += nrOfBytesToPrint;
		}
		return result.toString();
	}

	private static int calculateNrOfBytesToPrint(final int max) {
		final int nrOfBytesToPrintOnOneRow = 30;
		int nrOfBytesToPrint = nrOfBytesToPrintOnOneRow;
		if (nrOfBytesToPrint > max) {
			nrOfBytesToPrint = max;
		}
		return nrOfBytesToPrint;
	}

	private static String bytesToString(final byte[] bytes, int startIndex,
			int nrOfbytesToPrint) {
		StringBuffer result = new StringBuffer();
		int i = 1;
		while (nrOfbytesToPrint-- > 0) {
			byte b = bytes[startIndex++];
			result.append(String.format("%02X ", b));
			if (i == 6) {
				if (nrOfbytesToPrint > 0) {
					result.append(". "); // delimiter
				}
				i = 1;
			} else {
				result.append(" "); // delimiter
				i++;
			}
		}
		return result.toString();
	}	

}
