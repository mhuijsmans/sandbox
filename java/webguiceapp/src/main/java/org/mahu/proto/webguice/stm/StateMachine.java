package org.mahu.proto.webguice.stm;

import javax.ws.rs.core.Response;

public class StateMachine implements IStateMachine {

    private final Object lock = new Object();

    @Override
    public Response execute(IRequestProvider requestProvider) {
        return requestProvider.get().execute();
    }

}
