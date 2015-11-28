package org.mahu.proto.pngtexteditor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Extract string from the byte array.
 * The byte array holds a number of encoded strings.
 * A string starts at a fixed offset (0, maxNrOfUint16*2, maxNrOfUint16*4, etc).  
 */
public class StringExtractor {
	final byte[] matlabPixelData;
	int offset=0;
	final int maxNrOfCharacters;
	final int nrOfBytesPerChannelRow;
	final int maxOffset;
	
	public StringExtractor(final byte[] matlabPixelData, final int maxNrOfUint16, final int maxNrOfStrings) {
		this.matlabPixelData = matlabPixelData;
		this.maxNrOfCharacters = (maxNrOfUint16 *2)-1;
		this.nrOfBytesPerChannelRow = maxNrOfUint16*2;
		this.maxOffset = nrOfBytesPerChannelRow * maxNrOfStrings;
	}
	
	public boolean hasNext() {
		 return offset<maxOffset && matlabPixelData[offset]!=0;
	}
	
	public String nextString() throws IOException {
		int length=0;
		for(int i=offset; matlabPixelData[i]!=0; i++) {
			length ++;
			if (length>maxNrOfCharacters) {
				throw new IOException("Invalid text png file, candidate string exceds max length");
			}
			if (offset>=maxOffset) {
				throw new IOException("Offset exceeds maxOffset");
			}
			
		}
		String s = new String(matlabPixelData, offset, length, StandardCharsets.US_ASCII); 
		offset+= nrOfBytesPerChannelRow;
		return s;
	}
	
}
