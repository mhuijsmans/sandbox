package org.mahu.proto.webresttest;

import static org.junit.Assert.assertTrue;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mahu.proto.jerseyjunittools.RestResource;
import org.mahu.proto.jerseyjunittools.RestResourceUri;
import org.mahu.proto.jerseyjunittools.SpringRestServiceRule;
import org.mahu.proto.jerseyjunittools.annotation.RestResourceInTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/application-test.xml" })
public class AppTest {

	@Inject
	ApplicationContext context;

	@Rule
	@Inject
	public SpringRestServiceRule restService;

//	@Named
//	public static class EmployeeControllerProvider extends AbstractBinder implements
//			Factory<EmployeeController> {
//		
//		@Inject
//		ApplicationContext context;
//
//		// Configure is called at registration
//		@Override
//		protected void configure() {
//			bindFactory(this).to(EmployeeController.class);
//		}
//
//		/**
//		 * Called for every REST method to resource that needs instance of
//		 * class.
//		 */
//		public EmployeeController provide() {
//			return context.getBean(EmployeeController.class);
//		}
//
//		public void dispose(final EmployeeController instance) {
//		}
//	}

	@Test
	@RestResourceInTest(resource = EmployeeController.class)
	// @RestProviderInTest(provider = EmployeeControllerProvider.class)	
	public void injectionWorksTest() {
		RestResource<String> resource = new RestResource<String>(
				new RestResourceUri(restService.getBaseURI(),
						"webservice/hello"), String.class);
		resource.setMediaType(MediaType.TEXT_PLAIN);
		//
		resource.doGet();
		assertTrue("Response=" + resource.getResponseCode(),
				resource.getResponseCode() == 200);
		assertTrue(resource.getData().equals(EmployeeController.HELLO_MSG));
	}
}
