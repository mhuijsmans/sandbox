package org.mahu.proto.pngpixeldatatest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

class PixelDataUtil {

	public static void assertBytesEquals(final byte[] expected,
			final byte[] found, final boolean printHexData) {
		assertTrue(expected != null);
		assertTrue(found != null);
		assertTrue(expected.length == found.length);
		if (printHexData) {
			print("Expected Bytes", expected);
			print("Found Bytes", found);
		}
		for (int i = 0; i < expected.length; i++) {
			assertEquals("Index: " + i, expected[i], found[i]);
		}
	}

	public static byte[] createSmallPixeldataRGBBitdepth16(final int nrOfRows,
			final int nrOfColumns) {
		byte[] b = new byte[nrOfRows * nrOfColumns * 3 * 2];
		int i = 0;
		byte channelValue = 1;
		for (int columnIdx = 0; columnIdx < nrOfColumns; columnIdx++) {
			for (int rowIdx = 0; rowIdx < nrOfRows; rowIdx++) {
				// RGB
				b[i++] = channelValue;
				b[i++] = channelValue++;
				b[i++] = channelValue;
				b[i++] = channelValue++;
				b[i++] = channelValue;
				b[i++] = channelValue++;
			}
		}
		// print("Generate pixeldata "+nrOfRows+" * "+nrOfColumns,b);
		return b;
	}

	public static void print(String msg, byte[] bytes) {
		System.out.println(msg);
		System.out.println(bytesToString(bytes,
				bytes.length < 256 ? bytes.length : 256));
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

	public static String bytesToString(final byte[] bytes, int max) {
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
		final int nrOfBytesToPrintOnOneRow = 16;
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
			if (i == 8) {
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
