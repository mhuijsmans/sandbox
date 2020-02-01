package org.mahu.proto;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AppTest {

	private IRestClientBuilder builder = new RestClientBuilder();

	@Test
	public void get_checkResult() {
		// given, injected IRestClientBuilder

		// when get resource
		String response = builder.create(Service.A).get();

		// then
		// check returned result
		assertEquals("hi", response);
	}
	
	@Test
	public void get_catchException() {
		// given, injected IRestClientBuilder

		// when get resource
		Result<String> result = builder.create(Service.A).catchExceptions().get();
		
				// then
		// check returned result
		assertEquals("hi", result.getResponse());
	}
	
	@Test
	public void delete() {
		// given, injected IRestClientBuilder

		// when get resource
		builder.create(Service.A).delete();
	}
	
	@Test
	public void post_requestBody() {
		// given, injected IRestClientBuilder

		// when get resource
		String requestBody = new String();
		builder.create(Service.A).post(requestBody);
	}
	
	@Test
	public void post_noRequestBody() {
		// given, injected IRestClientBuilder

		// when get resource
		String requestBody = new String();
		builder.create(Service.A).post();
	}
	
}
