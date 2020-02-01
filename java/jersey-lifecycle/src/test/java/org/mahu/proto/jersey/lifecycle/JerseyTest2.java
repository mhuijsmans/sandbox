package org.mahu.proto.jersey.lifecycle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.ServletDeploymentContext;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class JerseyTest2 extends JerseyTest {
	
	private ServletContainer container;
	
	@Override
	protected  DeploymentContext configureDeployment() {
		// @formatter:off
		ResourceConfig config = new ResourceConfig(
				RestartRestService.class,				
				RestService.class);
		// @formatter:on		
		container = new ServletContainer(config);
		return ServletDeploymentContext.forServlet(container)
				.addListener(MyAppServletContextListener2.class)
				// .contextParam("contextConfigLocation", "classpath:test.xml")
				// .initParam("jersey.config.server.provider.packages", "uk.ac.ox.oucs.vle.resources;org.codehaus.jackson.jaxrs")
				.build();
	}	
	

	// Use heavy-weight container, because design & test uses servletcontext.
	// Use bit and pieces from:
	// https://stackoverflow.com/questions/26751854/jerseytest-using-grizzlywebtestcontainerfactory-in-jersey-2-13
	@Override
	protected TestContainerFactory getTestContainerFactory() {
		return new GrizzlyWebTestContainerFactory();
	}
	
	@Test
	public void restart() {
		// given
		// when
		Response response = target("/test/restart").request().post(null);
		// then
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		String content = response.readEntity(String.class);
		assertEquals(Const.OK, content);
	}	

}
