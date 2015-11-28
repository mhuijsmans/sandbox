package org.mahu.proto.socketthroughputtest.basic;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TcpClient {

	private final static int TIMEOUT = 10;

	public static Socket connect(final int port) throws IOException {
		InetSocketAddress address = new InetSocketAddress(
				InetAddress.getLocalHost(), port);
		return connect(address);
	}

	public static Socket connect(InetSocketAddress address) throws IOException {
		Socket socket = new Socket();
		socket.connect(address, TIMEOUT);
		return socket;
	}

}
