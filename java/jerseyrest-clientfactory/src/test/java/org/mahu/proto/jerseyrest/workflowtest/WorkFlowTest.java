package org.mahu.proto.jerseyrest.workflowtest;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;

public class WorkFlowTest extends JerseyTest {

    private final static Object lock = new Object();
    private static IRequestProcessingRules rules = new RequestProcessingRules();

    protected static void setRules(IRequestProcessingRules rules) {
        synchronized (lock) {
            WorkFlowTest.rules = rules;
        }
    }

    @Path("/")
    public static class HelloResource {

        @GET
        @Path("{ops: .*}")
        public Response getRequest(@PathParam("ops") final List<PathSegment> ops) {
            return getResponse(HttpMethod.GET, ops);
            // final String path = createPath(ops);
            // return Response.ok().entity(path).build();
        }

        @DELETE
        @Path("{ops: .*}")
        public Response deleteRequest(@PathParam("ops") final List<PathSegment> ops) {
            return getResponse(HttpMethod.DELETE, ops);
        }

        @POST
        @Path("{ops: .*}")
        public Response postRequest(@PathParam("ops") final List<PathSegment> ops) {
            return getResponse(HttpMethod.POST, ops);
        }

        @PUT
        @Path("{ops: .*}")
        public Response putRequest(@PathParam("ops") final List<PathSegment> ops) {
            return getResponse(HttpMethod.PUT, ops);
        }

    }

    @Override
    protected Application configure() {
        return new ResourceConfig(HelloResource.class);
    }

    protected String getTargetUrl() {
        return "http://127.0.0.1:" + getPort();
    }

    private static Response getResponse(String method, final List<PathSegment> ops) {
        if (ops.isEmpty()) {
            // throw
            // uncheckedException(ErrorCodeGeneral.NOT_FOUND).withField("reason",
            // "subpath after /internal missing")
            // .build();
            throw new RuntimeException("ops empty");
        }
        String path = createPath(ops);
        synchronized (lock) {
            return rules.getResponse(method, path);
        }
    }

    private static String createPath(final List<PathSegment> pathSegments) {
        final StringBuilder sb = new StringBuilder();
        for (final PathSegment pathSegment : pathSegments) {
            sb.append("/").append(pathSegment.getPath());
        }
        return sb.toString();
    }
}