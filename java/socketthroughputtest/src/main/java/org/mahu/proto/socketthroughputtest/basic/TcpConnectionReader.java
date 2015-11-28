package org.mahu.proto.socketthroughputtest.basic;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class TcpConnectionReader extends ConnectionBase  {
	private final InputStream is;

	public TcpConnectionReader(final Socket socket) throws IOException {
		super(socket);
		is = socket.getInputStream();
	}

	@Override
	public void run() {
		try {
		while (true) {
			int bytesRead = is.read(buffer);
			if (bytesRead<0) {
				break;
			}
			bytesRead(buffer, bytesRead);
			cnt += bytesRead;
		}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
			System.out.println("Data Read: "+cnt);
		}
	}

	public void bytesRead(byte[] buffer, int bytesRead) {
	}

}
