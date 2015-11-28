package org.mahu.proto.pngpixeldatatest;

import org.junit.Test;
import org.mahu.proto.commons.Chrono;
import org.mahu.proto.pngmisc.PixelDataConverter;
import org.mahu.proto.pngmisc.PixelDataConverterMT;

public class PixelDataConverterMtTest {

	private static boolean printBytesData = false;

	/**
	 * This test case compared the implementation of the MT version against the
	 * ST version. Current MT (stupid) implementation makes it slower
	 */
	@Test
	public void test_RGB16_Interleaved_to_MatlabColumnPlanar_to_Interleaved() {
		// Preparation
		final int nrOfRows = 1300;
		final int nrOfColumns = 3100;
		do_Interleaved_to_MatlabColumnPlanar_to_Interleaved(nrOfRows,
				nrOfColumns);
	}

	// First run
	// - time st : 225 ms
	// - time mt : 434
	// - time mt : 187
	// last run
	// - time st : 190 ms
	// - time mt : 385/591
	// - time mt : 80
	@Test
	public void test_RGB16_Interleaved_to_MatlabColumnPlanar_to_Interleaved_many() {
		// Preparation
		for (int i = 0; i < 10; i++) {
			final int nrOfRows = 1300;
			final int nrOfColumns = 3100;
			do_Interleaved_to_MatlabColumnPlanar_to_Interleaved(nrOfRows,
					nrOfColumns);
		}
	}

	protected void do_Interleaved_to_MatlabColumnPlanar_to_Interleaved(
			final int nrOfRows, final int nrOfColumns) {
		byte[] interleavedPixelData1 = PixelDataUtil
				.createSmallPixeldataRGBBitdepth16(nrOfRows, nrOfColumns);
		//
		PixelDataConverter converter1 = new PixelDataConverter(nrOfRows,
				nrOfColumns, interleavedPixelData1);
		Chrono chrono = new Chrono();
		byte[] matlabColumnPlanarPixelData1 = converter1
				.InterleavedRbg16Bit_to_matlabColumnPlanarRbg16BitPixelData();
		long time1 = chrono.elapsedMs();
		chrono.reset();
		//
		PixelDataConverterMT converter2 = new PixelDataConverterMT(nrOfRows,
				nrOfColumns, interleavedPixelData1);
		byte[] matlabColumnPlanarPixelData2 = converter2
				.InterleavedRbg16Bit_to_matlabColumnPlanarRbg16BitPixelData_mt();
		long time2 = chrono.elapsedMs();
		chrono.reset();
		//
		PixelDataConverterMT converter3 = new PixelDataConverterMT(nrOfRows,
				nrOfColumns, interleavedPixelData1);
		converter3.dummy_mt();
		long time3 = chrono.elapsedMs();
		chrono.reset();
		//
		PixelDataUtil.assertBytesEquals(matlabColumnPlanarPixelData1,
				matlabColumnPlanarPixelData2, printBytesData);
		System.out.println("time st : " + time1);
		System.out.println("time mt : " + time2);
		System.out.println("time mt : " + time3);
	}

}