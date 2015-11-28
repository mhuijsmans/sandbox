package org.mahu.proto.systemtest;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.junit.Rule;
import org.junit.Test;
import org.mahu.proto.jerseyjunittools.ResourceProvider;
import org.mahu.proto.jerseyjunittools.RestResource;
import org.mahu.proto.jerseyjunittools.RestServiceRule;
import org.mahu.proto.jerseyjunittools.annotation.RestProviderInTest;
import org.mahu.proto.jerseyjunittools.annotation.RestResourceInTest;
import org.mahu.proto.systemtest.adapplication.ADController;
import org.mahu.proto.systemtest.adapplication.ADControllerImpl;
import org.mahu.proto.systemtest.adapplication.ADControllerProvider;
import org.mahu.proto.systemtest.adapplication.StartTaskResource;
import org.mahu.proto.systemtest.logging.Logging;

public class ADRestTest {
	
	public static void setLoggingProperties() {
		System.setProperty("java.util.logging.config.file","target/test-classes/logging.properties");
		// This must be the first statement, so logging is in place before it is used.
		Logging.assertThatIfLoggingPropertyIsSetThatItExists();
	}

	@Rule
	public RestServiceRule restService = new RestServiceRule(CommonConst.AD_PORT);
	
	@Path(CommonConst.AD_CONTEXT)
	public final static  class StartTaskResource1 {

		 private ADController controller;
		
		 @Inject
		 public void setHandler(final ADController controller) {
			 this.controller = controller;
		 }

		@GET
		@Produces("text/plain")
		public String start() {
			controller.start();
			return "Work Completed";
		}

	}
	
	public final static class ADControllerProvider1 extends
	ResourceProvider<ADController, ADControllerImpl> {
}

	@Test
	@RestResourceInTest(resource = StartTaskResource1.class)
	@RestProviderInTest(provider = ADControllerProvider1.class)
	public void testMethodGet1() {
		RestResource<String> resource = RestUtils.startCallToRunApplications();
		assertEquals(200, resource.getResponseCode());
	}
	
	@Test
	@RestResourceInTest(resource = StartTaskResource.class)
	@RestProviderInTest(provider = ADControllerProvider.class)
	public void testMethodGet2() {
		RestResource<String> resource = RestUtils.startCallToRunApplications();
		assertEquals(200, resource.getResponseCode());
	}

}
