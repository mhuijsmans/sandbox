package org.mahu.proto.socketthroughputtest.nio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class BlockingNioTcpClient {

	private final static int TIMEOUT = 10;

	public static SocketChannel connect(final int port) throws IOException {
		InetSocketAddress address = new InetSocketAddress(
				InetAddress.getLocalHost(), port);
		return connect(address);
	}

	public static SocketChannel connect(final InetSocketAddress address)
			throws IOException {
		SocketChannel mySocket = SocketChannel.open();
		// todo: settings a timeout on connect is not possible
		// But if running on same machine, that is not an issue.
		mySocket.connect(address);
		return mySocket;
	}

}
