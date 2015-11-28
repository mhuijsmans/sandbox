package org.mahu.proto.pngmisc;

import static com.google.common.base.Preconditions.checkArgument;

public class PixelDataConverter {

	// These are the assumed facts
	protected final static int NO_OF_CHANNELS = 3; // RGB
	protected final static int NO_OF_BYTES_PER_CHANNEL = 2; // bitdepth 16
	protected final static int NO_OF_BYTES_PER_PIXEL = NO_OF_BYTES_PER_CHANNEL
			* NO_OF_CHANNELS;

	static class PixelDataConverterException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		PixelDataConverterException(String msg) {
			super(msg);
		}

		PixelDataConverterException(String msg, Exception e) {
			super(msg, e);
		}
	}

	protected final int nrOfRows;
	protected final int nrOfColumns;
	// In interleaved, pixel data is stored (per pixel): RGB ... RGB
	// Note: spaces are for readability
	// In matlab column pixel data data is stored as R..RG..GB..B, where the
	// channels
	// are column oriented
	protected final byte[] srcPixelData;

	public PixelDataConverter(final int nrOfRows, final int nrOfColumns,
			final byte[] pixelDataIn) {
		checkArgument(nrOfRows * nrOfColumns * NO_OF_BYTES_PER_PIXEL == pixelDataIn.length);
		this.nrOfRows = nrOfRows;
		this.nrOfColumns = nrOfColumns;
		this.srcPixelData = pixelDataIn;
	}

	public byte[] matlabColumnPlanarRbg16BitPixelData_to_InterleavedRbg16Bit() {
		byte[] rgbInterleavedPixeldata = new byte[srcPixelData.length];
		RbgIndex idx = new MatlabColumnPlanar_to_InterleavedIndex(nrOfRows,
				nrOfColumns);
		transformPixelData(rgbInterleavedPixeldata, idx);
		return rgbInterleavedPixeldata;
	}

	public byte[] InterleavedRbg16Bit_to_matlabColumnPlanarRbg16BitPixelData() {
		byte[] matlabColumnPlanarPixelData = new byte[srcPixelData.length];
		RbgIndex idx = new Interleaved_to_MatlabColumnPlanarIndex(nrOfRows,
				nrOfColumns);
		transformPixelData(matlabColumnPlanarPixelData, idx);
		return matlabColumnPlanarPixelData;
	}

	private void transformPixelData(final byte[] destPixeldata,
			final RbgIndex idx) {
		int i = 0;
		byte[] tmpSrc = srcPixelData;
		for (int columnIdx = 0; columnIdx < nrOfColumns; columnIdx++) {
			for (int rowIdx = 0; rowIdx < nrOfRows; rowIdx++) {
				for (int j = 0; j < NO_OF_BYTES_PER_PIXEL; j++) {
					final int index = idx.nextIndexInSourcePixeldata();
					byte b = tmpSrc[index];
					destPixeldata[i++] = b;
				}
			}
		}
	}

	/**
	 * This interface is used to generate in index for the source pixelData.
	 */
	static interface RbgIndex {
		public int nextIndexInSourcePixeldata();
	}

	public static class MatlabColumnPlanar_to_InterleavedIndex implements
			RbgIndex {
		final int nrOfPixels;
		final int offsetR;
		final int offsetG;
		final int offsetB;
		final int bytesPerChannel;
		final int bytesPerChannelColumn;
		int rowNr;
		int channelByteOffset;
		int pixelBytesCntr;
		int iR;
		int iG;
		int iB;

		public MatlabColumnPlanar_to_InterleavedIndex(final int nrOfRows,
				final int nrOfColumns) {
			this.nrOfPixels = nrOfRows * nrOfColumns;
			offsetR = 0;
			offsetG = nrOfPixels * NO_OF_BYTES_PER_CHANNEL;
			offsetB = 2 * offsetG;
			bytesPerChannel = nrOfPixels * NO_OF_BYTES_PER_CHANNEL;
			bytesPerChannelColumn = nrOfRows * NO_OF_BYTES_PER_CHANNEL;
			rowNr = 0;
			channelByteOffset = 0;
			pixelBytesCntr = 0;
			setIndicesRGB();
		}

		@Override
		public int nextIndexInSourcePixeldata() {
			int currentPixelBytesCntr = pixelBytesCntr++;
			int idx;
			switch (currentPixelBytesCntr) {
			case 0:
				idx = iR++;
				break;
			case 1:
				idx = iR;
				break;
			case 2:
				idx = iG++;
				break;
			case 3:
				idx = iG;
				break;
			case 4:
				idx = iB++;
				break;
			case 5:
				idx = iB;
				updateCountersAndIndices();
				break;
			default:
				throw new PixelDataConverterException("Implementation error");
			}
			return idx;
		}

		private void updateCountersAndIndices() {
			pixelBytesCntr = 0;
			channelByteOffset += bytesPerChannelColumn;
			if (channelByteOffset >= bytesPerChannel) {
				rowNr++;
				channelByteOffset = rowNr * NO_OF_BYTES_PER_CHANNEL;
			}
			setIndicesRGB();
		}

		protected void setIndicesRGB() {
			iR = offsetR + channelByteOffset;
			iG = offsetG + channelByteOffset;
			iB = offsetB + channelByteOffset;
		}
	}

	// Implemented logic
	// - input is interleaved RGB
	// - first provides indices for all R, next all G and next all B
	// - provided indices column wise (this orientation is matlab specific).
	public static class Interleaved_to_MatlabColumnPlanarIndex implements
			RbgIndex {
		final int nrOfColumns;
		final int nrOfRows;
		final int nrOfBytesPerRow;
		int channelOffsetInPixel;
		int channelByteIdx;
		int rowIdx;
		int columnIdx;
		int idx;

		public Interleaved_to_MatlabColumnPlanarIndex(final int nrOfRows,
				final int nrOfColumns) {
			this.nrOfColumns = nrOfColumns;
			this.nrOfRows = nrOfRows;
			channelByteIdx = 0;
			nrOfBytesPerRow = nrOfColumns * NO_OF_BYTES_PER_PIXEL;
			channelOffsetInPixel = 0;
			columnIdx = 0;
			updateIndices();
		}

		public int nextIndexInSourcePixeldata() {
			if (rowIdx >= nrOfRows) {
				updateIndices();
			}
			int returnedIdx;
			if (channelByteIdx == 0) {
				channelByteIdx++;
				returnedIdx = idx;
			} else {
				channelByteIdx = 0;
				rowIdx++;
				returnedIdx = idx + 1;
				idx += nrOfBytesPerRow;
			}
			return returnedIdx;
		}

		protected void updateIndices() {
			rowIdx = 0;
			if (columnIdx >= nrOfColumns) {
				// one channel has been fully processed. Move to next channel
				columnIdx = 0;
				channelOffsetInPixel += NO_OF_BYTES_PER_CHANNEL;
			}
			idx = columnIdx * NO_OF_BYTES_PER_PIXEL + channelOffsetInPixel;
			columnIdx++;
		}
	}

}
