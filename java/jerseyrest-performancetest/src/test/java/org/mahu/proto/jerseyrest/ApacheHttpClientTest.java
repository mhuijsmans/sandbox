package org.mahu.proto.jerseyrest;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;
import org.mahu.proto.commons.Chrono;
import org.mahu.proto.jerseyjunittools.annotation.RestResourceInTest;

public class ApacheHttpClientTest extends TestBaseClass {

	@Test
	@RestResourceInTest(resource = HelloWorld2Resource.class)
	public void testApacheHttpClient() throws IOException {

		final String imageName = "start.png";
		String adress = restService.getBaseURI().toURL().toString()
				+ "helloworld/images/" + imageName;
		final int MAX =50;
		final Chrono chrono = new Chrono();

		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpGet httpget = new HttpGet(adress);

			System.out.println("Executing request " + httpget.getRequestLine());

			// Create a custom response handler
			MyResponseHandler responseHandler = new MyResponseHandler();

			Info info = new Info();
			for (int j = 0; j < 2; j++) {
				for (int i = 0; i < MAX; i++) {
					info = httpclient.execute(httpget,
							responseHandler);
				}
				printProgressReportWithMsg(
						(responseHandler.copyToNio ? "With NIO wrap"
								: "With NIO copy"), chrono, MAX, info.imageSize);
				responseHandler.copyToNio = false;
			}

		} finally {
			httpclient.close();
		}
	}

	static class Info {
		public long imageSize = 0;
		public long length = 0;
	}

	static class MyResponseHandler implements ResponseHandler<Info> {

		public boolean copyToNio = true;

		public Info handleResponse(final HttpResponse response)
				throws ClientProtocolException, IOException {
			Info info = new Info();
			int status = response.getStatusLine().getStatusCode();
			if (status >= 200 && status < 300) {
				info.length = response.getEntity().getContentLength();
				InputStream is = response.getEntity().getContent();
				
				//response.getEntity().writeTo(arg0);

				info.imageSize = readFromInputStreamUsingNIO(copyToNio, buf,
						info.length, is);

			} else {
				throw new ClientProtocolException(
						"Unexpected response status: " + status);
			}
			//
			return info;
		}
	};
}
