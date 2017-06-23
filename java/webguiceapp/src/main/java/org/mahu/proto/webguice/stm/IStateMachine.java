package org.mahu.proto.webguice.stm;

import javax.ws.rs.core.Response;

public interface IStateMachine {

    public Response execute(IRequestProvider requestProvider);

}
