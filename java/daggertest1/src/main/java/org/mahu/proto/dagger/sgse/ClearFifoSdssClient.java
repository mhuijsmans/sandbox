package org.mahu.proto.dagger.sgse;

public class ClearFifoSdssClient extends GenericRESTClient {

    public ClearFifoSdssClient(final String url) {
        super(url);
    }

    public void sendClearFifo() throws SGSEException {
        sendDeleteRequest();
    }
}
