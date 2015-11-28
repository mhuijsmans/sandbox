package org.mahu.proto.jerseyrest;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.client.methods.AsyncByteConsumer;
import org.apache.http.nio.client.methods.HttpAsyncMethods;
import org.apache.http.protocol.HttpContext;
import org.junit.Test;
import org.mahu.proto.commons.Chrono;
import org.mahu.proto.jerseyjunittools.annotation.RestResourceInTest;

public class ApacheAsyncHttpClientTest extends TestBaseClass {

	@Test
	@RestResourceInTest(resource = HelloWorld2Resource.class)
	public void testApacheHttpClient() throws IOException,
			InterruptedException, ExecutionException {

		final String imageName = "start.png";
		String adress = restService.getBaseURI().toURL().toString()
				+ "helloworld/images/" + imageName;
		final int MAX = 50;
		final Chrono chrono = new Chrono();

		CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault();
		// Create a custom response handler
		MyResponseConsumer responseHandler = new MyResponseConsumer();
		try {
			httpclient.start();
			for (int i = 0; i < MAX; i++) {
				responseHandler = new MyResponseConsumer();
				Future<Boolean> future = httpclient.execute(
						HttpAsyncMethods.createGet(adress), responseHandler,
						null);
				Boolean result = future.get();
				// if (result != null && result.booleanValue()) {
				// System.out.println("Request successfully executed");
				// } else {
				// System.out.println("Request failed");
				// }
			}
			System.out.println("Shutting down");
		} finally {
			httpclient.close();
		}

		printProgressReport(chrono, MAX, responseHandler.total);

		System.out.println("Done");
	}

	static class Info {
		public long imageSize = 0;
		public long length = 0;
	}

	static class MyResponseConsumer extends AsyncByteConsumer<Boolean> {
		long total = 0;

		@Override
		protected void onByteReceived(ByteBuffer bb, IOControl arg1)
				throws IOException {
			// in a run I always got capacity 8192 (socket buffer size)
			//
			// System.out
			// .println("AsyncByteConsumer.onByteReceived: bb.capacity: "
			// + bb.capacity());
			
			// isDirect returns false; which explains why this implementation is
			// not blazing fast. It is NOT direct.
//			System.out
//					.println("AsyncByteConsumer.onByteReceived: bb.isDirect: "
//							+ bb.isDirect());
			total += bb.limit();
			bb.clear();
		}

		@Override
		protected Boolean buildResult(HttpContext arg0) throws Exception {
			// System.out
			// .println("AsyncByteConsumer.buildResult. total: " + total);
			return true;
		}

		@Override
		protected void onResponseReceived(HttpResponse arg0)
				throws HttpException, IOException {
			// System.out.println("AsyncByteConsumer.onResponseReceived. total: "
			// + total);

		}

	}
}
