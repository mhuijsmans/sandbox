package org.mahu.proto.pngmisc;

abstract class PngBase {
	
	protected final static int BITDEPTH8 = 8; // 8 bytes per channel
	protected final static int BITDEPTH16 = 16; // 2 bytes per channel
	protected final static int BYTES_PER_CHANNEL = 2; // 2 bytes per channel
	protected final static int NO_OF_CHANNELS = 3; // RGB
	protected final static int BYTES_PER_PIXEL = NO_OF_CHANNELS
			* BYTES_PER_CHANNEL;
	protected final static boolean NO_ALPHA = false;
	protected final static boolean NOT_INDEXED = false;
}
