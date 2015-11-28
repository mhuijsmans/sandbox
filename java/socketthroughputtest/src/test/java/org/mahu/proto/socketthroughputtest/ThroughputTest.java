package org.mahu.proto.socketthroughputtest;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Ignore;
import org.junit.Test;
import org.mahu.proto.socketthroughputtest.basic.TcpClient;
import org.mahu.proto.socketthroughputtest.basic.TcpConnectionReader;
import org.mahu.proto.socketthroughputtest.basic.TcpConnectionWriter;
import org.mahu.proto.socketthroughputtest.basic.TcpServer;
import org.mahu.proto.socketthroughputtest.nio.BlockingNioTcpClient;
import org.mahu.proto.socketthroughputtest.nio.BlockingNioTcpConnectionReader;
import org.mahu.proto.socketthroughputtest.nio.BlockingNioTcpConnectionWriter;
import org.mahu.proto.socketthroughputtest.nio.BlockingNioTcpServer;

public class ThroughputTest extends Const implements TcpServer.Listener,
		BlockingNioTcpServer.Listener  {
	private final static boolean DEBUG = true;
	private ExecutorService executor = Executors.newFixedThreadPool(4);
	public static String test = "?";

	@Test
	public void run1() throws IOException, InterruptedException {
		for (int i = 0; i < 5; i++) {
			clientAndServerInOne();
			blockingNioClientAndServerInOne();
			blockingNioClientAndBlockingNioServerInOne();
		}
	}

	// @Test(timeout = 30000)
	public void clientAndServerInOne() throws IOException, InterruptedException {
		test = "clientAndServerInOne           ";
		TcpServer server = new TcpServer(this);
		executor.execute(server);
		TimeUnit.SECONDS.sleep(1);
		testClient();
		server.close();
		TimeUnit.SECONDS.sleep(1);
	}

	// @Test(timeout = 30000)
	public void blockingNioClientAndServerInOne() throws IOException,
			InterruptedException {
		test = "blockingNioClientAndServerInOne";
		TcpServer server = null;
		try {
			server = new TcpServer(this);
			executor.execute(server);
			TimeUnit.SECONDS.sleep(1);
			testBlockingNioClient();
		} finally {
			closeServer(server);
			TimeUnit.SECONDS.sleep(1);
		}
	}

	public void blockingNioClientAndBlockingNioServerInOne() throws IOException,
			InterruptedException {
		test = "blockingNioClientAndBlockingNioServerInOne";
		BlockingNioTcpServer server = null;
		try {
			server = new BlockingNioTcpServer(this);
			executor.execute(server);
			TimeUnit.SECONDS.sleep(1);
			testBlockingNioClient();
		} finally {
			closeServer(server);
		}		
	}

	@Ignore
	@Test(timeout = 30000)
	public void testServer() throws IOException, InterruptedException {
		test = "testServer";
		TcpServer server = null;
		try {
			server = new TcpServer(this);
			executor.execute(server);
			TimeUnit.SECONDS.sleep(30);
		} finally {
			closeServer(server);
		}
	}

	@Ignore
	@Test(timeout = 30000)
	public void testClient() throws IOException, InterruptedException {
		Socket socket = null;
		try {
			socket = TcpClient.connect(TcpServer.PORT);
			TcpConnectionWriter writer = new TcpConnectionWriter(socket);
			byte[] b = new byte[BUFFER_SIZE];
			for (int i = 0; i < MAX; i++) {
				writer.write(b);
			}
			TimeUnit.SECONDS.sleep(1);
		} finally {
			if (socket != null) {
				socket.close();
			}
			System.out.println("testClient ready");
		}
	}

	@Test(timeout = 60000)
	public void testBlockingNioServer() throws IOException,
			InterruptedException {
		test = "testBlockingNioServer";
		BlockingNioTcpServer server = null;
		try {
			server = new BlockingNioTcpServer(this);
			executor.execute(server);
			TimeUnit.SECONDS.sleep(60);
		} finally {
			closeServer(server);
		}
	}

	@Test(timeout = 60000)
	public void testBlockingNioClient() throws IOException,
			InterruptedException {
		SocketChannel socket = null;
		try {

			socket = BlockingNioTcpClient.connect(TcpServer.PORT);
			BlockingNioTcpConnectionWriter writer = new BlockingNioTcpConnectionWriter(
					socket);
			// ByteBuffer b = ByteBuffer.allocate(BUFFER_SIZE);
			ByteBuffer b = ByteBuffer.allocateDirect(BUFFER_SIZE);
			for (int i = 0; i < MAX; i++) {
				b.clear();
				b.position(BUFFER_SIZE); // fake a write
				b.flip();
				writer.write(b);
			}
			TimeUnit.SECONDS.sleep(1);
		} finally {
			if (socket != null) {
				socket.close();
			}
			System.out.println("testblockingNioClient ready");
		}
	}

	@Override
	public void handleConnection(final Socket connection) {
		if (DEBUG)
			System.out.println("Accepted new connection");
		try {
			TcpConnectionReader reader = new MyTcpConnectionReader(connection);
			executor.execute(reader);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void handleConnection(final SocketChannel connection) {
		if (DEBUG)
			System.out.println("Accepted new nioConnection");
		try {
			MyBlockingNioTcpConnectionReader reader = new MyBlockingNioTcpConnectionReader(
					connection);
			executor.execute(reader);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void closeServer(TcpServer server) {
		if (server != null) {
			server.close();
		}
	}

	private void closeServer(BlockingNioTcpServer server) {
		if (server != null) {
			server.close();
		}
	}

	static class MyTcpConnectionReader extends TcpConnectionReader {
		private DataCounter cntr = new DataCounter(test);

		public MyTcpConnectionReader(final Socket socket) throws IOException {
			super(socket);
		}

		public void bytesRead(byte[] buffer, int bytesRead) {
			cntr.bytesRead(cnt, bytesRead);
		}
	}

	static class MyBlockingNioTcpConnectionReader extends
			BlockingNioTcpConnectionReader {
		private DataCounter cntr = new DataCounter(test);

		public MyBlockingNioTcpConnectionReader(final SocketChannel socket)
				throws IOException {
			super(socket);
		}

		public void bytesRead(final ByteBuffer buffer, int bytesRead) {
			cntr.bytesRead(cnt, bytesRead);
		}
	}

}
