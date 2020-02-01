package org.mahu.proto.jersey.proxy;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Ignore;
import org.junit.Test;
import org.mahu.proto.jersey.proxy.service.Const;
import org.mahu.proto.jersey.proxy.service.RestService;

public class JerseyProxyTest extends JerseyTest {

	@Override
	protected Application configure() {
		return new ResourceConfig(RestService.class);
	}

	// A request without arguments
	@Test
	@Ignore
	public void requestWithNoArguments_thenResponseIsOkAndContainsHi() {
		Response response = target("/test/info").request().get();

		assertEquals("Http Response should be 200: ", Status.OK.getStatusCode(), response.getStatus());
		assertEquals("Http Content-Type should be: ", MediaType.TEXT_PLAIN,
				response.getHeaderString(HttpHeaders.CONTENT_TYPE));

		String content = response.readEntity(String.class);
		assertEquals("Content of ressponse is: ", Const.HELLO, content);
	}

	// A request with arguments
	@Test
	@Ignore
	public void requestWithArgument_whenCorrectRequest_thenResponseIsOkAndContainsHi() {
		final String input = "input.";
		Response response = target("/test/more-info").request().post(Entity.entity(input, MediaType.TEXT_PLAIN));

		assertEquals("Http Response should be 200: ", Status.OK.getStatusCode(), response.getStatus());
		assertEquals("Http Content-Type should be: ", MediaType.TEXT_PLAIN,
				response.getHeaderString(HttpHeaders.CONTENT_TYPE));

		String content = response.readEntity(String.class);
		assertEquals("Content of ressponse is: ", input + Const.HELLO, content);
	}

	// A request specific Guice Module is used to resolve dependencies
	@Test
	@Ignore
	public void requestWithModule_whenCorrectRequest_thenResponseIsOkAndContainsHi() {
		Response response = target("/test/module-info").request().get();

		assertEquals("Http Response should be 200: ", Status.OK.getStatusCode(), response.getStatus());
		assertEquals("Http Content-Type should be: ", MediaType.TEXT_PLAIN,
				response.getHeaderString(HttpHeaders.CONTENT_TYPE));

		String content = response.readEntity(String.class);
		assertEquals("Content of ressponse is: ", Const.MODULE_HELLO, content);
	}

	// A request specific Guice Module is used to resolve dependencies
	@Test
	@Ignore
	public void requestWithRequestScopedData_thenResponseIsOkAndContainsHi() {
		Response response = target("/test/request-scoped-info").request().get();

		assertEquals("Http Response should be 200: ", Status.OK.getStatusCode(), response.getStatus());
		assertEquals("Http Content-Type should be: ", MediaType.TEXT_PLAIN,
				response.getHeaderString(HttpHeaders.CONTENT_TYPE));

		String content = response.readEntity(String.class);
		assertEquals("Content of ressponse is: ", Const.REQUEST_SCOPED_DATA_OVERRULE, content);
	}

	// On LapTop: 10.000 in 33 sec = 3.3 ms / request
	@Test
	@Ignore
	public void enduranceRun() {
		for (int i = 0; i < 10 * 1000; i++) {
			Response response = target("/test/request-scoped-info").request().get();

			assertEquals("Http Response should be 200: ", Status.OK.getStatusCode(), response.getStatus());
		}
	}

}
