package org.mahu.proto.webguice.request;

import javax.ws.rs.core.Response;

import org.mahu.proto.webguice.stm.IRequest;

// Request has package scope.
class GetRequest implements IRequest {

    @Override
    public Response execute() {
        return Response.ok("Hello World!").build();
    }

}
