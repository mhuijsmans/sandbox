package org.mahu.proto.pngtexteditor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.Test;
import org.mahu.proto.pngmisc.PixelDataConverter;

public class MainTest {
	@Test
	public void exploreString() {
		String s = "12345";
		assertTrue(s.length() == 5);
		assertTrue(s.getBytes(StandardCharsets.US_ASCII).length == 5F);
	}

	@Test
	public void encodingDecodingTest() throws IOException {
		String filename = "a.png";
		String str1 = "aaaaaaaaaa";
		String str2 = "bbbbbbbbbbb";
		AttrList attrs = new AttrList();
		attrs.add(str1);
		attrs.add(str2);
		TextPngUtil writer = new TextPngUtil(attrs);
		writer.encodeTheAtttributesInPixelData16BitRGB(filename);
		byte[] interleavedPixelData = writer.getPixelData();
		//
		int nrOfRows = writer.getNrOfRows();
		int nrOfColums = writer.getNrOfColumns();
		PixelDataConverter converter = new PixelDataConverter(nrOfRows,
				nrOfColums, interleavedPixelData);
		byte[] matlabPixelData = converter
				.InterleavedRbg16Bit_to_matlabColumnPlanarRbg16BitPixelData();
		StringExtractor extractor = new StringExtractor(matlabPixelData,
				nrOfRows, nrOfColums);
		assertEquals(TextPngUtil.SGS_MAGIX_HEADER, extractor.nextString());
		assertEquals(filename, extractor.nextString());
		assertEquals(str1, extractor.nextString());
		assertEquals(str2, extractor.nextString());
	}

	@Test
	public void pngEncodingDecodingTest() throws IOException {
		String filename = "test.png";
		String[] str = { "a", "aaaaaaaaaa", "bbbbbbbbbbb", "bb" };
		AttrList attrs1 = new AttrList();
		for (String s : str) {
			attrs1.add(s);
		}
		//
		TextPngUtil writer = new TextPngUtil(attrs1);
		byte[] interleavedPixelData = writer.createPng(filename);
		//
		AttrList attrs2 = new AttrList();
		TextPngUtil reader = new TextPngUtil(attrs2);
		boolean result = reader.readFrom(interleavedPixelData, filename);
		assertEquals(true, result);
		//
		for (String s : str) {
			Integer key = findKey(attrs2, s);
			// there is no delete key, as hat is not needed for the app.
			// I also decided to not add it for this test case.
			attrs2.update(key, "");
			attrs2.removeEmptyFields();
		}
		assertEquals(0, attrs2.size());
	}

	private Integer findKey(AttrList attrs, String value) {
		for(Integer key : attrs.getKeys()) {
			if (attrs.getValue(key).equals(value)) {
				return key;
			}
		}
		fail("Value not found in list: "+value);
		return null;
	}

}
