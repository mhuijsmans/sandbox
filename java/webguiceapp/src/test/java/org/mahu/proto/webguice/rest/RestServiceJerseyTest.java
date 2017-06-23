package org.mahu.proto.webguice.rest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.ServletDeploymentContext;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.junit.Test;
import org.mahu.proto.webguice.restconfig.ApplicationInitializeDestroy;

// Example Jersey test
// Source: http://docs.huihoo.com/jersey/2.13/test-framework.html 
public class RestServiceJerseyTest extends JerseyTest {

    @Path("hello")
    public static class HelloResource {
        @GET
        public String getHello() {
            return "Hello World!";
        }
    }

    @Override
    protected TestContainerFactory getTestContainerFactory() {
        return new GrizzlyWebTestContainerFactory();
    }

    @Override
    protected DeploymentContext configureDeployment() {
        ResourceConfig config = new ResourceConfig(RestService.class);
        return ServletDeploymentContext.forServlet(new ServletContainer(config))
                .addListener(ApplicationInitializeDestroy.class).build();
    }

    @Test
    public void testGet() {
        final String hello = target("test").request().get(String.class);
        assertEquals("Hello World!", hello);
    }

    @Test
    public void testPost() {
        final String msg = "Hello ServerWorld!";
        Entity<String> userEntity = Entity.entity(msg, MediaType.TEXT_PLAIN_TYPE);
        final String hello = target("test").request().post(userEntity, String.class);
        assertEquals(msg, hello);
    }
}