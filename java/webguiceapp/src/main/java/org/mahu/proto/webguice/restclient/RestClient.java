package org.mahu.proto.webguice.restclient;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

class RestClient implements IRestClient {

    @AssistedInject
    RestClient(@Assisted final String url) {
    }

    @Override
    public void post(Object data) {
    }

}
