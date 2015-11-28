package org.mahu.proto.embeddedjetty;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.logging.Logger;

import org.junit.Test;

public class RESTServerTest {

	final static Logger logger = java.util.logging.Logger
			.getLogger(RESTServerTest.class.getName());

	private static int PORT = 9100;
	private static String BASEURL = "http://localhost:" + PORT;

	@Test(timeout = 3000)
	public void doGet_correctURL_200Ok() throws Exception {
		RESTServer server = new RESTServer();
		try {
			// preparation
			server.start(PORT, TESTService.class.getName());
			{
				// test
				HttpClient httpClient = new HttpClient();
				String url = BASEURL + "/hello";
				httpClient.doGet(url);
				// verify
				assertTrue(httpClient.getResponseCode() == 200);
				assertEquals(TESTService.RESPONSE1, httpClient.response);
				logger.info("responsecode=" + httpClient.getResponseCode());
				logger.info("response    =" + httpClient.response);
			}
			{
				// test
				HttpClient httpClient = new HttpClient();
				String url = BASEURL + "/hello/hello";
				httpClient.doGet(url);
				// verify
				assertTrue(httpClient.getResponseCode() == 200);
				assertEquals(TESTService.RESPONSE2,
						httpClient.response);
				logger.info("responsecode=" + httpClient.getResponseCode());
				logger.info("response    =" + httpClient.response);
			}
		} finally {
			server.stop();
		}
		String name = new Object(){}.getClass().getEnclosingMethod().getName();
		System.out.println("name="+name);
	}

}
