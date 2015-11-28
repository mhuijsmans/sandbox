package org.mahu.proto.socketthroughputtest.basic;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpServer implements Runnable {
	private final static boolean DEBUG = false;
	public final static int PORT = 19999;
	private final Listener listener;
	private ServerSocket socket = null;

	public interface Listener {
		public void handleConnection(Socket connection);
	}

	public TcpServer(final Listener listener) {
		this.listener = listener;
	}

	public void run() {
		try {	
			socket = new ServerSocket();
			socket.setReuseAddress(true);
			InetSocketAddress address = new InetSocketAddress(
					InetAddress.getLocalHost(), PORT);				
			socket.bind(address);
			while (true) {
				Socket connection = socket.accept();
				listener.handleConnection(connection);
			}
		} catch (java.net.SocketException e) {
			if (socket != null && socket.isClosed()) {
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
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}
}
