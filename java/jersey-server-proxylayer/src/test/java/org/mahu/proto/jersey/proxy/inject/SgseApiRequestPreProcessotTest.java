package org.mahu.proto.jersey.proxy.inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Ignore;
import org.junit.Test;
import org.mahu.proto.jersey.proxy.request.ApplicationModule;
import org.mahu.proto.jersey.proxy.request.IRequestAnnotationScopedRequestDataProviderThrowsException;
import org.mahu.proto.jersey.proxy.request.IRequestNoArguments;
import org.mahu.proto.jersey.proxy.request.IRequestThrowsException;
import org.mahu.proto.jersey.proxy.request.IRequestWithoutBinding;
import org.mahu.proto.jersey.proxy.request.IRequestWithArgument;
import org.mahu.proto.jersey.proxy.request.IRequestWithChildModule;
import org.mahu.proto.jersey.proxy.request.IRequestWithRequestScopedDataDefault;
import org.mahu.proto.jersey.proxy.request.IRequestWithRequestScopedDataOverrule;
import org.mahu.proto.jersey.proxy.service.Const;

import com.google.inject.Guice;

public class SgseApiRequestPreProcessotTest {

	private static final ApplicationModule module = new ApplicationModule();
	private static final IRequestPreProcessor requestPreProcessor = new RequestPreProcessor(
			Guice.createInjector(module));

	@Test
	public void with_requestNoArguments_correctResponse() {
		String response = requestPreProcessor.with(IRequestNoArguments.class).getResponse();

		assertEquals(Const.REQUEST_NO_ARGUMENTS, response);
	}

	@Test
	public void with_requestWithArguments_correctResponse() {
		final String input = "input.";

		String response = requestPreProcessor.with(IRequestWithArgument.class).getResponse(input);

		assertEquals(input + Const.REQUEST_WITH_ARGUMENT, response);
	}

	@Test
	public void with_requestWithChildModule_correctResponse() {
		String response = requestPreProcessor.with(IRequestWithChildModule.class).getResponse();

		assertEquals(Const.REQUEST_WITH_CHILD_MODULE, response);
	}

	@Test
	public void with_requestWithRequestScopedDataOverrule_correctResponse() {
		String response = requestPreProcessor.with(IRequestWithRequestScopedDataOverrule.class).getResponse();

		assertEquals(Const.REQUEST_SCOPED_DATA_OVERRULE, response);
	}

	@Test
	public void with_requestWithRequestScopedDataDefault_correctResponse() {
		String response = requestPreProcessor.with(IRequestWithRequestScopedDataDefault.class).getResponse();

		assertEquals(Const.REQUEST_SCOPED_DATA_DEFAULT, response);
	}

	@Test
	public void with_requestWithoutBinding_exception() {
		try {
			requestPreProcessor.with(IRequestWithoutBinding.class).getResponse();
			fail("Exception expected, because there is no binding for the interface");
		} catch (RuntimeException e) {
			// TODO: improve
			assertTrue(e != null);
		}
	}
	
	@Test
	public void with_requestThrowsException_exception() {
		try {
			requestPreProcessor.with(IRequestThrowsException.class).getResponse();
			fail("Exception expected, because request throws an exception");
		} catch (RuntimeException e) {
			// TODO: improve
			assertEquals(IllegalStateException.class, e.getCause().getCause().getCause().getClass());
		}
	}
	
	@Test
	public void with_requestAnnotationScopedRequestDataProviderThrowsException_exception() {
		try {
			requestPreProcessor.with(IRequestAnnotationScopedRequestDataProviderThrowsException.class).getResponse();
			fail("Exception expected, because request throws an exception");
		} catch (RuntimeException e) {
			// TODO: improve
			assertEquals(IllegalAccessException.class, e.getCause().getClass());
		}
	}	

	@Test
	@Ignore
	public void with_durationTest() {
		final ApplicationModule module = new ApplicationModule();
		final IRequestPreProcessor requestPreProcessor = new RequestPreProcessor(Guice.createInjector(module));
		for (int i = 0; i < 1000 * 1000 * 1000; i++) {
			String response = requestPreProcessor.with(IRequestNoArguments.class).getResponse();

			assertEquals(Const.REQUEST_NO_ARGUMENTS, response);
		}
	}

}
