package org.mahu.proto.webguice.stm;

import javax.ws.rs.core.Response;

import com.google.inject.AbstractModule;

public interface IIRequestProcessor {

    public Response execute(RequestType requestType, AbstractModule childModule);

}
