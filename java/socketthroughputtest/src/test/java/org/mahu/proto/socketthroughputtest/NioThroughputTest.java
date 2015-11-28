package org.mahu.proto.socketthroughputtest;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.mahu.proto.forkprocesstest.ChildProcess;
import org.mahu.proto.socketthroughputtest.basic.TcpServer;
import org.mahu.proto.socketthroughputtest.nio.BlockingNioTcpClient;
import org.mahu.proto.socketthroughputtest.nio.BlockingNioTcpConnectionReader;
import org.mahu.proto.socketthroughputtest.nio.BlockingNioTcpConnectionWriter;
import org.mahu.proto.socketthroughputtest.nio.BlockingNioTcpServer;

public class NioThroughputTest extends Const {
	private final static boolean DEBUG = true;

	private ExecutorService executor = Executors.newFixedThreadPool(3);

	@Test
	public void testBlockingNioClient() throws IOException,
			InterruptedException, ExecutionException {
		SocketChannel socket = null;

		ChildProcess fork = new ChildProcess();
		fork.printChildDataToConsole();
		FutureTask<Integer> future = fork.createTaskCloneOwnProcess(
				org.junit.runner.JUnitCore.class, NioServer.class.getName());
		executor.execute(future);
		TimeUnit.SECONDS.sleep(1);
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
			fork.destroyChild();
			printOutput(fork.getOutputData());
			printOutput(fork.getErrorData());
			System.out.println("testblockingNioClient ready");
		}
	}

	private void printOutput(List<String> data) {
		int l = data.size();
		int i = 0;
		while (i < l) {
			System.out.println(data.get(i++));
		}
	}

	public final static class NioServer implements
			BlockingNioTcpServer.Listener {

		private static String test = "?";
		private ExecutorService executor = Executors.newFixedThreadPool(3);

		@Test
		public void testBlockingNioServer() throws IOException,
				InterruptedException {
			test = "testBlockingNioServer";
			BlockingNioTcpServer server = null;
			try {
				server = new BlockingNioTcpServer(this);
				executor.execute(server);
				TimeUnit.SECONDS.sleep(120);
			} finally {
				closeServer(server);
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

		private void closeServer(BlockingNioTcpServer server) {
			if (server != null) {
				server.close();
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

}
