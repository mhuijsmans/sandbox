package org.mahu.proto.dagger.sgse;

import javax.inject.Inject;

public final class Tesk implements IExecutableTask {

    @Inject
    SGSEUrl sgseUrl;

    @Override
    public void execute() throws SGSEException {
        final ClearFifoSdssClient client = new ClearFifoSdssClient(sgseUrl.getUrlSdssFimFifo());
        client.sendClearFifo();
    }
}
