package org.mahu.proto.jerseyrest.workflowtest;

import java.util.Optional;

import javax.ws.rs.core.Response;

class ResponseBuilder {
    static Response createResponse(final int status) {
        return Response.status(status).build();
    }

    static Response createResponse(final int status, final Optional<Object> responseBody) {
        return responseBody.isPresent() ? Response.status(status).entity(responseBody.get()).build()
                : createResponse(status);
    }
}