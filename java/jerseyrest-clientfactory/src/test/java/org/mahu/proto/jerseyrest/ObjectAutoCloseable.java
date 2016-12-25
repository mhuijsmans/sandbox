package org.mahu.proto.jerseyrest;

import javax.ws.rs.core.Response;

public class ObjectAutoCloseable implements AutoCloseable {

    private final Response response;

    ObjectAutoCloseable(Response response) {
        this.response = response;
    }

    @Override
    public void close() {
        response.close();
    }

}
