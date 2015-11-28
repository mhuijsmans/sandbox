package org.mahu.proto.jerseyjunittools;

import static org.junit.Assert.assertTrue;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.junit.Rule;
import org.junit.Test;
import org.mahu.proto.jerseyjunittools.annotation.RestProviderInTest;
import org.mahu.proto.jerseyjunittools.annotation.RestResourceInTest;

/**
 * Test explores use of the abstract binder
 */
public class MyProviderTest {

	private final static String GET_RESPONSE = "JAX-RS-GET";

	@Rule
	public RestServiceRule restService = new RestServiceRule();

	static class HelloWorldHandlerImpl implements HelloWorldHandler {
		public String getText() {
			return GET_RESPONSE;
		}
	}

	public interface HelloWorldHandler {
		public String getText();
	}

	@Path("helloworld")
	public static class HelloWorldResource {

		private HelloWorldHandler handler;

		@Inject
		public void setHandler(final HelloWorldHandler aHandler) {
			handler = aHandler;
		}

		@GET
		@Produces(MediaType.TEXT_PLAIN)
		public String doGet() {
			System.out.println(HelloWorldResource.class.getSimpleName()
					+ ".GET");
			return handler.getText();
		}

	}

	//
	// class has to be final, static & public.
	// - Final, because see ResourceProvider;
	// - public & static, because class instance is created by external class  

	public final static class HelloWorldHandlerProvider extends
			ResourceProvider<HelloWorldHandler, HelloWorldHandlerImpl> {
	}

	@Test
	@RestResourceInTest(resource = HelloWorldResource.class)
	@RestProviderInTest(provider = HelloWorldHandlerProvider.class)
	public void testMethodGet() {
		RestResource<String> resource = new RestResource<String>(
				new RestResourceUri(restService.getBaseURI(), "helloworld"),
				String.class);
		resource.setMediaType(MediaType.TEXT_PLAIN);
		//
		resource.doGet();
		assertTrue("Response=" + resource.getResponseCode(),
				resource.getResponseCode() == 200);
		assertTrue(resource.getData().equals(GET_RESPONSE));
		//
		// Test repeated to see behaviour of provider
		resource.doGet();
		assertTrue("Response=" + resource.getResponseCode(),
				resource.getResponseCode() == 200);
		assertTrue(resource.getData().equals(GET_RESPONSE));
	}

}
