package org.mahu.proto.socketthroughputtest.basic;

import java.io.IOException;
import java.net.Socket;

abstract class ConnectionBase implements Runnable {

	protected static int BUFFER_SIZE = 64 * 1024;

	private final Socket socket;
	protected byte[] buffer = new byte[BUFFER_SIZE];
	protected long cnt = 0;

	ConnectionBase(final Socket socket) throws IOException {
		this.socket = socket;
	}

	public void run() {
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
