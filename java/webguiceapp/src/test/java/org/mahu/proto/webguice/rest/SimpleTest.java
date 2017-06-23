package org.mahu.proto.webguice.rest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

// Example Jersey test
// Source: http://docs.huihoo.com/jersey/2.13/test-framework.html 
public class SimpleTest extends JerseyTest {

    @Path("hello")
    public static class HelloResource {
        @GET
        public String getHello() {
            return "Hello World!";
        }
    }

    @Override
    protected Application configure() {
        return new ResourceConfig(HelloResource.class);
    }

    @Test
    public void test() {
        final String hello = target("hello").request().get(String.class);
        assertEquals("Hello World!", hello);
    }
}