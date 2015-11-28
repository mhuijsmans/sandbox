package org.mahu.proto.jerseyjunittools;

import static org.junit.Assert.assertTrue;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.junit.Rule;
import org.junit.Test;
import org.mahu.proto.jerseyjunittools.annotation.RestResourceInTest;

/**
 * A test showing basics
 */
public class HelloWorldTest {

	private final static String GET_RESPONSE = "JAX-RS-GET";
	private final static String DELETE_RESPONSE = "JAX-RS-DELETE";

	@Rule
	public RestServiceRule restService = new RestServiceRule();

	@Path("/helloworld")
	// Note that class must be public
	public static class HelloWorldResource {
		/**
		 * Method handling HTTP GET requests, returning a "text/plain" media
		 * type.
		 */
		@GET
		@Produces(MediaType.TEXT_PLAIN)
		public String doGet() {
			System.out.println(HelloWorldResource.class.getSimpleName()
					+ ".GET");
			return GET_RESPONSE;
		}

		@DELETE
		@Produces(MediaType.TEXT_PLAIN)
		public String doDelete() {
			System.out.println(HelloWorldResource.class.getSimpleName()
					+ ".DELETE");
			return DELETE_RESPONSE;
		}
	}
	
	@Path("/helloworld1")
	// Note that class must be public
	public static class HelloWorldResourceSimple {
		/**
		 * Method handling HTTP GET requests, returning a "text/plain" media
		 * type.
		 */
		@GET
		@Produces(MediaType.TEXT_PLAIN)
		public String doGet() {
			return GET_RESPONSE;
		}
	}	

	@Test
	@RestResourceInTest(resource = HelloWorldResource.class)
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
	}

	@Test
	@RestResourceInTest(resource = HelloWorldResourceSimple.class)
	public void testDelayMethodGet() {
		callServerMaxTimes(1);
		callServerMaxTimes(10);
		callServerMaxTimes(100);
		callServerMaxTimes(500);
	}

	protected void callServerMaxTimes(int MAX) {
		long now = System.currentTimeMillis();
		for (int i = 0; i < MAX; i++) {
			RestResource<String> resource = new RestResource<String>(
					new RestResourceUri(restService.getBaseURI(), "helloworld1"),
					String.class);
			resource.setMediaType(MediaType.TEXT_PLAIN);
			//
			resource.doGet();
			assertTrue("Response=" + resource.getResponseCode(),
					resource.getResponseCode() == 200);
		}
		long elapsedTime = System.currentTimeMillis() - now;
		System.out.println("max: "+MAX+", elapsedTime: " + elapsedTime+ ", avg (ms): "+(elapsedTime/MAX));
	}

	@Test
	@RestResourceInTest(resource = HelloWorldResource.class)
	public void testMethodDelete() {
		RestResource<String> resource = new RestResource<String>(
				new RestResourceUri(restService.getBaseURI(), "helloworld"),
				String.class);
		resource.setMediaType(MediaType.TEXT_PLAIN);
		//
		resource.doDelete();
		assertTrue("Response=" + resource.getResponseCode(),
				resource.getResponseCode() == 200);
		assertTrue(resource.getData().equals(DELETE_RESPONSE));
	}
}
