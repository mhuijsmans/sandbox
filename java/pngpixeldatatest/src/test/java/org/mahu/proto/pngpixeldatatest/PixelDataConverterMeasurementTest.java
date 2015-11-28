package org.mahu.proto.pngpixeldatatest;

import org.junit.Test;
import org.mahu.proto.commons.Chrono;
import org.mahu.proto.pngmisc.PixelDataConverter;

public class PixelDataConverterMeasurementTest {

	@Test
	public void test_RGB16_Interleaved_to_MatlabColumnPlanar_to_Interleaved() {
		for (int i = 0; i < 10; i++) {
			doDoubleConversion();
		}
	}

	protected void doDoubleConversion() {
		final int nrOfRows = 1300;
		final int nrOfColumns = 3100;
		Chrono chrono = new Chrono();
		byte[] interleavedPixelData1 = PixelDataUtil
				.createSmallPixeldataRGBBitdepth16(nrOfRows, nrOfColumns);
		long elapsedTimeCreate = chrono.elapsedMs();
		chrono.reset();
		//
		PixelDataConverter converter1 = new PixelDataConverter(nrOfRows,
				nrOfColumns, interleavedPixelData1);
		byte[] matlabColumnPlanarPixelData1 = converter1
				.InterleavedRbg16Bit_to_matlabColumnPlanarRbg16BitPixelData();
		long elapsedTimeConvertToMatlab = chrono.elapsedMs();
		chrono.reset();
		//
		PixelDataConverter converter2 = new PixelDataConverter(nrOfRows,
				nrOfColumns, matlabColumnPlanarPixelData1);
		converter2.matlabColumnPlanarRbg16BitPixelData_to_InterleavedRbg16Bit();
		long elapsedTimeConvertfromMatlab = chrono.elapsedMs();
		chrono.reset();
		//
		System.out.println("elapsedTimeCreate            : "
				+ elapsedTimeCreate);
		System.out.println("elapsedTimeConvertToMatlab   : "
				+ elapsedTimeConvertToMatlab);
		System.out.println("elapsedTimeConvertfromMatlab : "
				+ elapsedTimeConvertfromMatlab);
	}
}