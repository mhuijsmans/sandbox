package org.mahu.proto.jerseyrest;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;
import org.mahu.proto.commons.Chrono;
import org.mahu.proto.jerseyjunittools.annotation.RestResourceInTest;
import org.mahu.proto.workmanager.WorkManager;

public class HttpUrlConnectionTest extends TestBaseClass {

	@Test
	@RestResourceInTest(resource = HelloWorld2Resource.class)
	public void testDirectUrlConnection() throws IOException {
		Chrono chrono = new Chrono();
		int MAX = 50;
		boolean copyToNio = true;
		long imageSize = -1;
		for (int j = 0; j < 2; j++) {
			for (int i = 0; i < MAX; i++) {
				final String imageName = "start.png";
				URL url = new URL(restService.getBaseURI().toURL().toString()
						+ "helloworld/images/" + imageName);
				HttpURLConnection con = (HttpURLConnection) url
						.openConnection();
				con.setConnectTimeout(10000); // set timeout to 10 seconds
				int responseCode = con.getResponseCode();
				assertEquals(HttpURLConnection.HTTP_OK, responseCode);
				//
				int length = con.getContentLength();
				InputStream is = con.getInputStream();
				//
				imageSize = readFromInputStreamUsingNIO(copyToNio, buf, length,
						is);
			}
			printProgressReportWithMsg((copyToNio ? "With NIO wrap"
					: "With NIO copy"), chrono, MAX, imageSize);
			copyToNio = false;
		}
	}

	@Test
	@RestResourceInTest(resource = HelloWorld2Resource.class)
	public void testDirectUrlConnectionParallel() throws IOException {
		Chrono chrono = new Chrono();
		long imageSize = 23000000;
		int N = 4;
		WorkManager workManager = new WorkManager(4);
		int MAX = 200;
		for (int i = 0; i < N; i++) {
			workManager.execute(new RetrieveTask(MAX));
		}
		workManager.waitForAllJobsToTerminate();
		printProgressReportWithMsg("With NIO wrap", chrono, MAX * N, imageSize);
	}

	static class RetrieveTask implements Runnable {
		public Exception e;
		final int MAX;

		RetrieveTask(int MAX) {
			this.MAX = MAX;
		}

		public void run() {
			try {
				boolean copyToNio = true;
				for (int i = 0; i < MAX; i++) {
					final String imageName = "start.png";
					URL url = new URL(me.restService.getBaseURI().toURL()
							.toString()
							+ "helloworld/images/" + imageName);
					HttpURLConnection con = (HttpURLConnection) url
							.openConnection();
					con.setConnectTimeout(10000); // set timeout to 10 seconds
					int responseCode = con.getResponseCode();
					assertEquals(HttpURLConnection.HTTP_OK, responseCode);
					//
					int length = con.getContentLength();
					InputStream is = con.getInputStream();
					//
					readFromInputStreamUsingNIO(copyToNio, buf, length, is);
				}
			} catch (Exception e) {
				this.e = e;
			}
		}

	}
}
