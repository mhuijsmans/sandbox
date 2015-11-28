package org.mahu.proto.pngmisc;

import java.io.ByteArrayInputStream;
import java.io.File;

import ar.com.hjg.pngj.ImageInfo;
import ar.com.hjg.pngj.ImageLineInt;

import com.google.common.base.Preconditions;

public class PngReader extends PngBase {

	private final ar.com.hjg.pngj.PngReader rdr;
	private ImageInfo imgInfo;
	private final ByteArrayInputStream bis;
	
	public PngReader(final File png) {
		rdr = new ar.com.hjg.pngj.PngReader(png);
		bis = null;
		this.imgInfo = rdr.imgInfo;
		checkThatPngFormatIsSupported();
	}

	public PngReader(final byte[] pngRawData) {
		this.bis = new ByteArrayInputStream(pngRawData);
		this.rdr = new ar.com.hjg.pngj.PngReader(bis);
		this.imgInfo = rdr.imgInfo;
		checkThatPngFormatIsSupported();
	}

	public int getNrOfRows() {
		return imgInfo.rows;
	}

	public int getNrOfColumns() {
		return imgInfo.cols;
	}
	
	public int getBitDepth() {
		return imgInfo.bitDepth;
	}	
	
	public byte[] getPixelDataAsBytes() {
		int nrOfBytes = calculateNrOfBytes(imgInfo);
		byte[] pixeldata = new byte[nrOfBytes];
		RbgIndex idx = new RbgInterleavedIndex();
		while (rdr.hasMoreRows()) {
			ImageLineInt row = (ImageLineInt) rdr.readRow();
			int[] scanLine = row.getScanline();
			// For bit-depth 8 /16, an int is used to store channel data
			for (int j : scanLine) {
				// PNG ByteOrder
				// ref: http://www.libpng.org/pub/png/spec/1.2/PNG-DataRep.html
				// states
				// All integers that require more than one byte must be in
				// network byte order: the most significant byte comes first,
				// then the less significant bytes in descending order of
				// significance.
				pixeldata[idx.getIndex()] = (byte) (j >> 8); // MSB
				pixeldata[idx.getIndex()] = (byte) (j >> 0); // LSB
				//
				// To store in Little Endian format
				// pixeldata[idx.getIndex()] = (byte) j;
				// pixeldata[idx.getIndex()] = (byte) (j >> 8);
				// Target system is Linux/i86, little endian
				// http://serverfault.com/questions/163487/linux-how-to-tell-if-system-is-big-endian-or-little-endian
			}
		}
		rdr.close();
		return pixeldata;
	}
	
	// Not used in project (sofar)
//	private int[] getPixelDataAsInts() {
//		int nrOfBytes = NO_OF_CHANNELS * imgInfo.rows * imgInfo.cols;
//		int[] pixeldata = new int[nrOfBytes];
//		int i=0;
//		while (rdr.hasMoreRows()) {
//			ImageLineInt row = (ImageLineInt) rdr.readRow();
//			int[] scanLine = row.getScanline();
//			// For bit-depth 8 /16, an int is used to store channel data
//			for (int j : scanLine) {
//				pixeldata[i++] = j;
//			}
//		}
//		rdr.close();
//		return pixeldata;
//	}	

	private int calculateNrOfBytes(final ImageInfo imgInfo) {
		long nrOfBytesLong = (BYTES_PER_PIXEL * imgInfo.rows * imgInfo.cols);
		int nrOfBytesint = (int) nrOfBytesLong;
		Preconditions.checkArgument(nrOfBytesint == nrOfBytesLong);
		return nrOfBytesint;
	}

	private void checkThatPngFormatIsSupported() {
		Preconditions.checkArgument(imgInfo.bitDepth == BITDEPTH8
				|| imgInfo.bitDepth == BITDEPTH16);
		Preconditions.checkArgument(imgInfo.channels == NO_OF_CHANNELS);
		Preconditions.checkArgument(imgInfo.alpha == NO_ALPHA);
		Preconditions.checkArgument(imgInfo.indexed == NOT_INDEXED);
	}

	static interface RbgIndex {
		public int getIndex();
	}

	static class RbgInterleavedIndex implements RbgIndex {
		int i = 0;

		public int getIndex() {
			return i++;
		}
	}

}
