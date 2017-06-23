package org.mahu.proto.webguice.stm;

import javax.ws.rs.core.Response;

public interface IRequestStateMachine {

    public Response execute(RequestType requestType, IRequestProvider provider);

}
