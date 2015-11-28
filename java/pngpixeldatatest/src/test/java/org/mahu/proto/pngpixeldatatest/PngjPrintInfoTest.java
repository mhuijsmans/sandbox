package org.mahu.proto.pngpixeldatatest;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import ar.com.hjg.pngj.ImageInfo;
import ar.com.hjg.pngj.ImageLineInt;

import com.google.common.base.Preconditions;

public class PngjPrintInfoTest {

	private ImageInfo imgInfo;
	private ar.com.hjg.pngj.PngReader rdr;

	/**
	 * This class print png info using pngj
	 */
	@Test
	public void printInfo() throws IOException {

		final String imageName = "/pathToTheImage/image.png";
		rdr = new ar.com.hjg.pngj.PngReader(new File(imageName));
		imgInfo = rdr.imgInfo;
		//
		int nrOfBytes = calculateNrOfBytes();
		byte[] pixeldata = new byte[nrOfBytes];
		//
		int i = 0;
		while (rdr.hasMoreRows()) {
			int[] scanLine = readOneRow();
			for (int j : scanLine) {
				// Target system is Linux/i86, little endian
				// http://serverfault.com/questions/163487/linux-how-to-tell-if-system-is-big-endian-or-little-endian
				pixeldata[i++] = (byte) j;
				pixeldata[i++] = (byte) (j >> 8);
			}
		}
		System.out.println("PixelData\n");
		System.out.println(PixelDataUtil.bytesToString(pixeldata, 256));
	}

	private int[] readOneRow() {
		ImageLineInt row = (ImageLineInt) rdr.readRow();
		int[] scanLine = row.getScanline();
		if (scanLine.length != imgInfo.cols * 3) {
			throw new RuntimeException("ScanLine array size is " + scanLine.length
					+ " but is expected to be 3 * " + imgInfo.cols);
		}
		return scanLine;
	}

	private int calculateNrOfBytes() {
		int assumedBitDepth = 16; // imgInfo.bitDepth
		long nrOfBytesLong = ((assumedBitDepth * imgInfo.channels) / 8)
				* imgInfo.rows * imgInfo.cols;
		int nrOfBytesint = (int) nrOfBytesLong;
		Preconditions.checkArgument(nrOfBytesint == nrOfBytesLong);
		return nrOfBytesint;
	}

}
