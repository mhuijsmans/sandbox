package org.mahu.proto.socketthroughputtest.basic;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class TcpConnectionWriter  extends ConnectionBase {
	private final OutputStream os;

	public TcpConnectionWriter(final Socket socket) throws IOException {
		super(socket);
		os = socket.getOutputStream();
	}
	
	public void write(final byte[]b) throws IOException {
		write(b, b.length );
	}


	public void write(final byte[]b, final int  length) throws IOException {
		os.write(b, 0, length);
	}
}
