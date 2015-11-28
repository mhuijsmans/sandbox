package org.mahu.proto.pngmisc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import ar.com.hjg.pngj.ImageInfo;
import ar.com.hjg.pngj.ImageLineInt;

public class PngWriter extends PngBase {
	
	private final int compressionLevel; 
	
	public PngWriter() {
		this(0);
	}

	public PngWriter(final int compressionLevel) {
		this.compressionLevel = compressionLevel;
	}

	public byte[] convertPixelDataToPng(final byte[] interleavedRgb,
			final int nrOfRows, final int nrOfColums, final int bitdepth) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(
				interleavedRgb.length);
		ImageInfo imgInfo = new ImageInfo(nrOfColums, nrOfRows, bitdepth,
				NO_ALPHA);
		ar.com.hjg.pngj.PngWriter writer = new ar.com.hjg.pngj.PngWriter(bos,
				imgInfo);
		writer.setCompLevel(compressionLevel);
		//
		int[] scanLine = new int[nrOfColums * NO_OF_CHANNELS];
		for (int offset = 0; offset < interleavedRgb.length;) {
			for (int i = 0; i < scanLine.length; i++) {
				// Byte array hold data in PNG format, i.e. network order, i.e.
				// MSB LSB
				int msb = interleavedRgb[offset++];
				int lsb = interleavedRgb[offset++];
				scanLine[i] = ((msb << 8) & 0xff00)	| (lsb & 0xff);
			}
			ImageLineInt line = new ImageLineInt(imgInfo, scanLine);
			writer.writeRow(line);
		}
		writer.end();
		bos.flush();
		return bos.toByteArray();
	}

}
