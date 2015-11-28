package org.mahu.proto.socketthroughputtest.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class BlockingNioTcpConnectionReader extends ConnectionBase {
	private final ByteBuffer buffer;

	public BlockingNioTcpConnectionReader(final SocketChannel socket)
			throws IOException {
		super(socket);
		buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
	}

	@Override
	public void run() {
		try {
			while (true) {
				int bytesRead = socket.read(buffer);
				if (bytesRead < 0) {
					break;
				}
				bytesRead(buffer, bytesRead);
				buffer.clear();
				cnt += bytesRead;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
			System.out.println("Data Read: " + cnt);
		}
	}

	public void bytesRead(final ByteBuffer buffer, int bytesRead) {
	}

}
