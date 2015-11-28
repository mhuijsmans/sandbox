package org.mahu.proto.pngpixeldatatest;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Test;
import org.mahu.proto.pngmisc.PixelDataConverter;
import org.mahu.proto.pngmisc.PixelDataConverter.Interleaved_to_MatlabColumnPlanarIndex;
import org.mahu.proto.pngmisc.PixelDataConverter.MatlabColumnPlanar_to_InterleavedIndex;

public class PixelDataConverterTest {

	private final static int NO_OF_CHANNELS = 3; // RGB
	private final static int NO_OF_BYTES_PER_CHANNEL = 2; // bitdepth 16
	private final static int NO_OF_BYTES_PER_PIXEL = NO_OF_BYTES_PER_CHANNEL
			* NO_OF_CHANNELS;

	private static boolean printBytesData = false;

	@Test
	public void test_MatlabColumnPlanar_to_InterleavedIndex() {
		// Matrix of 2*2 with interleaved-RGB and 14 bitDepth color
		int[] expected = new int[] { 0, 1, 8, 9, 16, 17, /**/4, 5, 12, 13, 20,
				21, /**/2, 3, 10, 11, 18, 19, /**/6, 7, 14, 15, 22, 23 };
		MatlabColumnPlanar_to_InterleavedIndex idx = new MatlabColumnPlanar_to_InterleavedIndex(
				2, 2);
		for (int i = 0; i < 2 * 2 * NO_OF_BYTES_PER_PIXEL; i++) {
			assertEquals("Index: " + i, expected[i],
					idx.nextIndexInSourcePixeldata());
		}
	}

	@Test
	public void test_Interleaved_to_MatlabColumnPlanarIndex2() {
		// Matrix of 2*2 with interleaved-RGB and 16 bitDepth color
		int[] expected = new int[] { /* R */0, 1, 12, 13, 6, 7, 18, 19, /* G */
		2, 3, 14, 15, 8, 9, 20, 21, /* B */4, 5, 16, 17, 10, 11, 22, 23 };
		Interleaved_to_MatlabColumnPlanarIndex idx = new Interleaved_to_MatlabColumnPlanarIndex(
				2, 2);
		for (int i = 0; i < expected.length; i++) {
			int index = idx.nextIndexInSourcePixeldata();
			// System.out.println("index="+index);
			assertEquals("index=" + i, expected[i], index);
		}
	}

	@Test
	public void test_RGB16_Interleaved_to_MatlabColumnPlanar_to_Interleaved() {
		// Preparation
		final int nrOfRows = 3;
		final int nrOfColumns = 4;
		do_Interleaved_to_MatlabColumnPlanar_to_Interleaved(nrOfRows,
				nrOfColumns);
	}

	@Test
	public void test_RGB16_Interleaved_to_MatlabColumnPlanar_to_Interleaved_random() {
		// Preparation
		for (int i = 0; i < 50; i++) {
			final int nrOfRows = generateRandom(5, 1000);
			final int nrOfColumns = generateRandom(5, 1000);
			do_Interleaved_to_MatlabColumnPlanar_to_Interleaved(nrOfRows,
					nrOfColumns);
		}
	}

	protected void do_Interleaved_to_MatlabColumnPlanar_to_Interleaved(
			final int nrOfRows, final int nrOfColumns) {
		byte[] interleavedPixelData1 = PixelDataUtil
				.createSmallPixeldataRGBBitdepth16(nrOfRows, nrOfColumns);
		if (printBytesData) {
			PixelDataUtil.printPixelData("interleavedPixelData1 ", nrOfRows,
					nrOfColumns, interleavedPixelData1);
		}
		//
		PixelDataConverter converter1 = new PixelDataConverter(nrOfRows,
				nrOfColumns, interleavedPixelData1);
		byte[] matlabColumnPlanarPixelData1 = converter1
				.InterleavedRbg16Bit_to_matlabColumnPlanarRbg16BitPixelData();
		if (printBytesData) {
			PixelDataUtil.printPixelData("matlabColumnPlanarPixelData ",
					nrOfRows, nrOfColumns, matlabColumnPlanarPixelData1);
		}
		//
		PixelDataConverter converter2 = new PixelDataConverter(nrOfRows,
				nrOfColumns, matlabColumnPlanarPixelData1);
		byte[] interleavedPixelData2 = converter2
				.matlabColumnPlanarRbg16BitPixelData_to_InterleavedRbg16Bit();
		if (printBytesData) {
			PixelDataUtil.printPixelData("interleavedPixelData2 ", nrOfRows,
					nrOfColumns, interleavedPixelData2);
		}
		//
		PixelDataUtil.assertBytesEquals(interleavedPixelData1,
				interleavedPixelData2, printBytesData);
		//
		PixelDataConverter converter3 = new PixelDataConverter(nrOfRows,
				nrOfColumns, interleavedPixelData2);
		byte[] matlabColumnPlanarPixelData2 = converter3
				.InterleavedRbg16Bit_to_matlabColumnPlanarRbg16BitPixelData();
		if (printBytesData) {
			PixelDataUtil.printPixelData("matlabColumnPlanarPixelData ",
					nrOfRows, nrOfColumns, matlabColumnPlanarPixelData2);
		}
		//
		PixelDataUtil.assertBytesEquals(matlabColumnPlanarPixelData1,
				matlabColumnPlanarPixelData2, printBytesData);		
	}

	private int generateRandom(int minimum, int maximum) {
		Random rn = new Random();
		int range = maximum - minimum + 1;
		int randomNum = rn.nextInt(range) + minimum;
		return randomNum;
	}

}