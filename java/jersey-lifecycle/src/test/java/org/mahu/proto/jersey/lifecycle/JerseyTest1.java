package org.mahu.proto.jersey.lifecycle;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
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

public class JerseyTest1 extends JerseyTest {
	
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
				.addListener(MyAppServletContextListener1.class)
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

	@Test
	public void info1() {
		// given
		// when
		Response response = target("/test/info1").request().get();
		// then
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		assertEquals(MediaType.TEXT_PLAIN, response.getHeaderString(HttpHeaders.CONTENT_TYPE));
		String content = response.readEntity(String.class);
		assertEquals(Const.HELLO, content);
	}
	
	@Test
	public void info2_info1NotCalled_responseIsStringUnknown() {
		// given // info1 has saved data in servletcontext
		// when
		Response response = target("/test/info2").request().get();
		// then
		String content = response.readEntity(String.class);
		assertEquals(Const.UNKNOWN, content);
	}	
	
	@Test
	public void info2_afterInfo1_returnsDataFromServletContext() {
		// given // info1 has saved data in servletcontext
		target("/test/info1").request().get();
		// when
		Response response = target("/test/info2").request().get();
		// then
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		assertEquals(MediaType.TEXT_PLAIN, response.getHeaderString(HttpHeaders.CONTENT_TYPE));
		String content = response.readEntity(String.class);
		assertEquals(Const.HELLO, content);
	}		

}
