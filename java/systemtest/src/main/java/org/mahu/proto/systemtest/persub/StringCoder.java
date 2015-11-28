package org.mahu.proto.systemtest.persub;

import java.nio.charset.Charset;

class StringCoder {
	private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

	static String decodeUTF8(byte[] bytes) {
		return new String(bytes, UTF8_CHARSET);
	}

	static String decodeUTF8(final byte[] bytes, final int offset,
			final int length) {
		return new String(bytes, offset, length, UTF8_CHARSET);
	}

	static byte[] encodeUTF8(final String string) {
		return string.getBytes(UTF8_CHARSET);
	}

}
