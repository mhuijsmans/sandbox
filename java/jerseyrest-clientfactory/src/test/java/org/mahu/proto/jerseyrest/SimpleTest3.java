package org.mahu.proto.jerseyrest;

import java.io.InputStream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.AfterClass;
import org.junit.Test;

public class SimpleTest3 extends JerseyTest {
    private static volatile String target;

    final int max = 100;
    private static Metrics metrics = new Metrics();

    @Path("/")
    public static class HelloResource {

        @GET
        @Path("hello1")
        public Response getHello1() {
            return performget("/hello4");
        }

        @GET
        @Path("hello2")
        public Response getHello2() {
            Response response = performget("/hello4");
            byte[] data = response.readEntity(byte[].class);
            response.close();
            return Response.ok().entity(data).build();
        }

        @GET
        @Path("hello3")
        public Response getHello3() {
            InputStream is = performget("/hello4").readEntity(InputStream.class);
            return Response.ok().entity(is).build();
        }

        @GET
        @Path("hello4")
        public Response getHello4() {
            return Response.ok(new byte[32 * 1020 * 1024]).build();
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
    public void test1() {
        target = getTarget();
        Metrics metrics = new Metrics();

        final int MAX = 100;

        // hello1 - totaltime(ms)=23,591 avg(microSec)=235,910, run=100
        // performRun(metrics, "hello1", MAX);

        // hello2 - totaltime(ms)=26,192 avg(microSec)=261,920, run=100
        // performRun(metrics, "hello2", MAX);

        // hello3 - totaltime(ms)=23,683 avg(microSec)=236,830, run=100
        // performRun(metrics, "hello3", MAX);

        // hello4 - totaltime(ms)=14,199 avg(microSec)=141,990, run=100
        // performRun(metrics, "hello4", MAX);

        performRun(metrics, "hello1", 1000);
        performRun(metrics, "hello2", 1000);
        performRun(metrics, "hello3", 1000);

        metrics.printInfo();
    }

    private void performRun(Metrics metrics, final String path, final int MAX) {
        StatisticsRun run = metrics.create(path, MAX);
        for (int i = 0; i < MAX; i++) {
            target("/" + path).request().get(byte[].class);
        }
        run.ready();
    }

    private static Response performget(String path) {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(target + path);
        Response response = webTarget.request().get();
        return response;
    }

    private String getTarget() {
        return "http://127.0.0.1:" + getPort();
    }

}