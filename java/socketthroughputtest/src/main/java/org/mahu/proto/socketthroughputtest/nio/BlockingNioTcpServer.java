package org.mahu.proto.socketthroughputtest.nio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class BlockingNioTcpServer implements Runnable {
	private final static boolean DEBUG = false;
	public final static int PORT = 19999;
	private final Listener listener;
	private ServerSocketChannel serverSocketChannel = null;

	public interface Listener {
		public void handleConnection(SocketChannel connection);
	}

	public BlockingNioTcpServer(final Listener listener) {
		this.listener = listener;
	}

	public void run() {
		try {
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR,
					true);
			InetSocketAddress address = new InetSocketAddress(
					InetAddress.getLocalHost(), PORT);
			serverSocketChannel.socket().bind(address);
			while (true) {
				SocketChannel connection = serverSocketChannel.accept();
				listener.handleConnection(connection);
			}
		} catch (java.nio.channels.AsynchronousCloseException e) {
			if (serverSocketChannel != null && !serverSocketChannel.isOpen()) {
				if (DEBUG)
					System.out.println("Socket closed");
			} else {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}

	public synchronized void close() {
		if (serverSocketChannel != null) {
			try {
				serverSocketChannel.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}
}
