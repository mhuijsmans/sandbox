package org.mahu.proto.embeddedtomcat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import org.apache.catalina.LifecycleException;
import org.junit.Test;
import org.mahu.proto.embeddedtomcat.HttpClient.HttpClientException;

public class RESTServerTest {

	final static Logger logger = java.util.logging.Logger
			.getLogger(RESTServerTest.class.getName());

	private static int PORT = 9100;
	private static String BASEURL = "http://localhost:" + PORT + "/target/rest";

	@Test(timeout = 3000)
	public void doGet_correctURL_200Ok() throws HttpClientException,
			InterruptedException {
		RESTServerThread server = new RESTServerThread();
		try {
			// preparation
			server.start();
			Thread.sleep(1000);
			// test
			HttpClient httpClient = new HttpClient();
			String url = BASEURL + "/hello";
			httpClient.doGet(url);
			// verify
			assertTrue(httpClient.getResponseCode() == 200);
			assertEquals(TESTService.RESPONSE, httpClient.response);
			logger.info("responsecode=" + httpClient.getResponseCode());
			logger.info("response    =" + httpClient.response);
		} finally {
			server.server.stop();
		}
	}

	class RESTServerThread extends Thread {
		RESTServer server = new RESTServer();

		public void run() {
			try {
				server.start(PORT, TESTService.class);
			} catch (MalformedURLException | ServletException
					| LifecycleException e) {
				e.printStackTrace();
			}
		}
	}

}
