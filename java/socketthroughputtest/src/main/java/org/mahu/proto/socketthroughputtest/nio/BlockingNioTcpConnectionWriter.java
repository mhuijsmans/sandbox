package org.mahu.proto.socketthroughputtest.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class BlockingNioTcpConnectionWriter {
	private static boolean DEBUG = false;
	private final SocketChannel socket;

	public BlockingNioTcpConnectionWriter(final SocketChannel socket) {
		this.socket = socket;
		if (!socket.isBlocking()) {
			throw new AssertionError("Only blocking SocketChannel supported");
		}
	}

	public void write(final ByteBuffer src) throws IOException {
		int nrOfBytesToWrite =  src.remaining();
		if (DEBUG) System.out.println("Bytes to write: "+nrOfBytesToWrite);
		int nrOfBytesWritten = socket.write(src);
		if (nrOfBytesWritten != nrOfBytesToWrite) {
			throw new AssertionError("Blocking Socket dodn't write all data");
		}
	}

}
