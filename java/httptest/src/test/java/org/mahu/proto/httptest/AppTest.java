package org.mahu.proto.httptest;

import static org.junit.Assert.assertTrue;

import org.apache.catalina.LifecycleException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mahu.proto.httptest.HttpClient.HttpClientException;

public class AppTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	/**
	 * Test thee embedded Tomcat: start, call, stop
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void callTomcat() throws LifecycleException, HttpClientException,
			InterruptedException {
		TomcatEmbeddedRunner server = new TomcatEmbeddedRunner();
		server.startServer();
		// test
		HttpClient httpClient = new HttpClient();
		String url = "http://localhost:" + server.getPort() + "/"
				+ server.getDatePath();
		httpClient.doGet(url);
		assertTrue(httpClient.getResponseCode() == 200);
		// test
		server.stop();
	    exception.expect(HttpClientException.class);
		httpClient.doGet(url);
	}

	/**
	 * Test thee embedded Jetty: start, call, stop
	 */
	@Test
	public void callJetty() throws Exception {
		// preparation
		JettyEmbeddedRunner server = new JettyEmbeddedRunner();
		server.startServer();
		// test
		HttpClient httpClient = new HttpClient();
		String url = "http://localhost:" + server.getPort() + "/"
				+ server.getDatePath();
		httpClient.doGet(url);
		assertTrue(httpClient.getResponseCode() == 200);
		server.stop();
		// test
		server.stop();
	    exception.expect(HttpClientException.class);
		httpClient.doGet(url);		
	}
}
