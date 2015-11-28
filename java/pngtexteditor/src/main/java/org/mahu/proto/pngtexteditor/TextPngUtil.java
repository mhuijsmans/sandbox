package org.mahu.proto.pngtexteditor;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.mahu.proto.commons.IOUtils;
import org.mahu.proto.pngmisc.PixelDataConverter;
import org.mahu.proto.pngmisc.PngReader;
import org.mahu.proto.pngmisc.PngWriter;

public class TextPngUtil {

	private static boolean DEBUG = false;

	public final static String SGS_MAGIX_HEADER = "SGS-VET";
	public static boolean USE_FIXED_SIZE = true;
	public static int PNG_WITDH = 1280;
	public static int PNG_HEIGHT = 3076;

	private final static int NR_OF_CHANNELS = 3;
	private final static int BITDEPTH = 16;
	private final static int NR_OF_BYTESCHANNEL = 2;
	private final static int NR_OF_BYTES_PER_PIXEL = NR_OF_CHANNELS
			* NR_OF_BYTESCHANNEL;
	private final AttrList attrs;
	private byte[] pixelData;
	private int nrOfColumns;
	private int nrOfRows;
	private int nrOfBytesPerRow;

	TextPngUtil(final AttrList attrs) {
		this.attrs = attrs;
	}

	public void encodeTheAtttributesInPixelData16BitRGB(final String filename) {
		nrOfColumns = calculateNrOfColumns();
		nrOfRows = calculateNrOfRows(filename);
		nrOfBytesPerRow = nrOfColumns * NR_OF_BYTES_PER_PIXEL;
		if (DEBUG) {
			System.out.println("nrOfColumns: " + nrOfColumns + " nrOfRows: "
					+ nrOfRows + " nrOfBytesPerRow: " + nrOfBytesPerRow);
		}
		pixelData = new byte[nrOfRows * nrOfBytesPerRow];
		encodeTextInColum(SGS_MAGIX_HEADER, 0);
		encodeTextInColum(filename, 1);
		int column = 2;
		for (Integer key : attrs.getKeys()) {
			String value = attrs.getValue(key);
			// there can be empty values, these are not written
			if (!value.isEmpty()) {
				encodeTextInColum(attrs.getValue(key), column);
				column++;
			}
		}
	}

	public int getNrOfRows() {
		return nrOfRows;
	}

	public int getNrOfColumns() {
		return nrOfColumns;
	}

	public byte[] getPixelData() {
		return pixelData;
	}

	/**
	 * Encode the text in column (of R channel), such that for after conversion
	 * to Matlab Planar Column based, the text is again a stream of bytes.
	 */
	private void encodeTextInColum(final String value, final int column) {
		if (DEBUG) {
			System.out.println("Encoding value: " + value + " column: "
					+ column);
		}
		int offset = column * NR_OF_BYTES_PER_PIXEL;
		byte[] b = value.getBytes(StandardCharsets.US_ASCII);
		for (int i = 0; i < b.length;) {
			pixelData[offset] = b[i++];
			// number of characters can be odd
			if (i < b.length) {
				pixelData[offset + 1] = b[i++];
				offset += nrOfBytesPerRow;
			}
		}
	}

	private int calculateNrOfColumns() {
		int count = 2; // magic header is always present and also filename
		for (String value : attrs.getValues()) {
			int l = value.length();
			if (l > 0) {
				count++;
			}
		}
		if (USE_FIXED_SIZE) {
			if (count>= PNG_WITDH) {
				throw new AssertionError("Too many string to encode"); 
			}
			count = PNG_WITDH;
		}
		return count;
	}

	private int calculateNrOfRows(final String filename) {
		int lengthLargestString = SGS_MAGIX_HEADER.length();
		lengthLargestString = max(lengthLargestString, filename.length());
		for (String value : attrs.getValues()) {
			lengthLargestString = max(lengthLargestString, value.length());
		}
		// string will be stored with trailing 0
		// and 2 characters / 16 bit channel
		int nrOfRows = ((lengthLargestString + 1) + 1) / 2;
		if (USE_FIXED_SIZE) {
			if (nrOfRows>= PNG_HEIGHT) {
				throw new AssertionError("String to encode exceeds maximum length"); 
			}
			nrOfRows = PNG_HEIGHT;
		}
		return nrOfRows;
	}

	private int max(int m1, int m2) {
		return (m1 > m2) ? m1 : m2;
	}

	public boolean writeTo(final File file) {
		try {
			byte[] png = createPng(file.getName());
			IOUtils.writeBytes(file, png);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	public byte[] createPng(final String filename) throws IOException {
		encodeTheAtttributesInPixelData16BitRGB(filename);
		int compressionLevel = 4; // through some experiment I have learned that
		// 4 gives great compression, is fast and more doesn't provide any gains.
		PngWriter writer = new PngWriter(compressionLevel);
		byte[] png = writer.convertPixelDataToPng(pixelData, nrOfRows,
				nrOfColumns, BITDEPTH);
		return png;
	}

	public boolean readFrom(final File tmpFile) {
		try {
			final byte[] png = IOUtils.readAllBytes(tmpFile);
			return readFrom(png, tmpFile.getName());
		} catch (IOException | RuntimeException e) {
			return false;
		}
	}

	public boolean readFrom(final byte[] png, final String filename) {
		try {
			PngReader rdr = new PngReader(png);
			byte[] interleavedPixelData = rdr.getPixelDataAsBytes();
			nrOfRows = rdr.getNrOfRows();
			nrOfColumns = rdr.getNrOfColumns();
			PixelDataConverter converter = new PixelDataConverter(nrOfRows,
					nrOfColumns, interleavedPixelData);
			byte[] matlabPixelData = converter
					.InterleavedRbg16Bit_to_matlabColumnPlanarRbg16BitPixelData();
			//
			StringExtractor extractor = new StringExtractor(matlabPixelData,
					nrOfRows, nrOfColumns);
			checkString(extractor, SGS_MAGIX_HEADER,
					"Invalid text png file, magic header not found");
			checkString(extractor, filename,
					"Invalid text png file, filename mismatch");
			while(extractor.hasNext()) {
				attrs.add(extractor.nextString());
			}
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	protected void checkString(StringExtractor extractor, String expectedValue,
			String errorMsg) throws IOException {
		if (!extractor.nextString().equals(expectedValue)) {
			throw new IOException(errorMsg);
		}
	}

}
