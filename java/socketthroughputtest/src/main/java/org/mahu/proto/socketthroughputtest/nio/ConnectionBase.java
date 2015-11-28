package org.mahu.proto.socketthroughputtest.nio;

import java.io.IOException;
import java.nio.channels.SocketChannel;

abstract class ConnectionBase implements Runnable {
	protected static int BUFFER_SIZE = 64 * 1024;

	protected final SocketChannel socket;
	protected long cnt = 0;

	ConnectionBase(final SocketChannel socket) throws IOException {
		this.socket = socket;
	}

	public void run() {
		// TODO Auto-generated method stub

	}

	public long getCnt() {
		return cnt;
	}

	protected void close() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
