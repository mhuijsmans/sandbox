package org.mahu.proto.webguice.restclient;

public interface IRestClientFactory {
    IRestClient create(String url);
}
