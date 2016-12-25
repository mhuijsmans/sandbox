package org.mahu.proto.jerseyrest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;

public class SimpleTest extends JerseyTest {

    final int max = 1000;
    private static Metrics metrics = new Metrics();

    public static class HelloResource {

        @GET
        @Path("hello")
        public Response getHello() {
            return Response.ok().header("Connection-close", "close").entity("Hello world").build();
        }
    }

    @Override
    protected Application configure() {
        return new ResourceConfig(HelloResource.class);
    }

    @AfterClass
    public static void afterClass() {
        metrics.printInfo();
    }

    @Test
    @Ignore
    public void test1() {
        final String hello = target("hello").request().get(String.class);
        assertEquals("Hello World!", hello);
    }

    @Test
    public void testThree() {
        for (int i = 0; i < 3; i++) {
            // @formatter:off
            testNewThree(      "Create three(" + i + ")      ");
            testNewTwo(        "Create two(" + i + ")        ");
            testNewOne(        "Create one(" + i + ")        ");
            testNewOneFullPath("Create oneFullPath(" + i + ")");
            testNewHash(       "Create hashMap(" + i + ")    ");
            testNewZero(       "Create zero(" + i + ")       ");
            // @formatter:off            
        }
    }

    public void testNewThree(final String name) {
        StatisticsRun stat = metrics.create(name, max);
        for (int i = 0; i < max; i++) {
            WebTarget target = ClientFactory.createClient().target(getTarget()).path("hello");
            Response response = null;
            try {
                response = target.request().get();
            } finally {
                if (response != null) {
                    response.close();
                }
            }
        }
        stat.ready();
    }

    public void testNewTwo(final String name) {
        StatisticsRun stat = metrics.create(name, max);
        Client client = ClientFactory.createClient();
        for (int i = 0; i < max; i++) {
            Response response = null;
            try {
                response = client.target(getTarget()).path("hello").request().get();
            } finally {
                if (response != null) {
                    response.close();
                }
            }
        }
        stat.ready();
    }

    public void testNewOne(final String name) {
        StatisticsRun stat = metrics.create(name, max);
        WebTarget target =  ClientFactory.createClient().target(getTarget());
        for (int i = 0; i < max; i++) {
            Response response = null;
            try {
                response = target.path("hello").request().get();
            } finally {
                if (response != null) {
                    response.close();
                }
            }
        }
        stat.ready();
    }

    public void testNewOneFullPath(final String name) {
        StatisticsRun stat = metrics.create(name, max);
        Client client = ClientFactory.createClient();
        for (int i = 0; i < max; i++) {
            Response response = null;
            WebTarget target = null;
            try {
                target = client.target(getTarget() + "/hello");
                response = target.request().get();
            } finally {
                if (response != null) {
                    response.close();
                }
            }
        }
        stat.ready();
    }

    public void testNewZero(final String name) {
        StatisticsRun stat = metrics.create(name, max);
        WebTarget target = ClientFactory.createClient().target(getTarget()).path("hello");
        for (int i = 0; i < max; i++) {
            Response response = null;
            try {
                response = target.request().get();
            } finally {
                if (response != null) {
                    response.close();
                }
            }
        }
        stat.ready();
    }

    public void testNewHash(final String name) {
        StatisticsRun stat = metrics.create(name, max);
        for (int i = 0; i < max; i++) {
            WebTarget target = ClientFactory.createWebTarget(getTarget(), "hello");
            Response response = null;
            try {
                response = target.request().get();
            } finally {
                if (response != null) {
                    response.close();
                }
            }
        }
        stat.ready();
    }

    private String getTarget() {
        return "http://127.0.0.1:" + getPort();
    }

}