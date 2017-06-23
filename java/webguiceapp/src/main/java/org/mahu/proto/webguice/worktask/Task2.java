package org.mahu.proto.webguice.worktask;

import org.mahu.proto.webguice.config.UrlConfig;
import org.mahu.proto.webguice.restclient.IRestClient;
import org.mahu.proto.webguice.restclient.IRestClientFactory;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class Task2 {

    private final UrlConfig urlConfig;
    private final Task2Data data;
    private final IRestClientFactory restClientFactory;

    @AssistedInject
    Task2(final UrlConfig urlConfig, final IRestClientFactory restClientFactory, @Assisted Task2Data data) {
        this.data = data;
        this.urlConfig = urlConfig;
        this.restClientFactory = restClientFactory;
    }

    public void execute() {
        IRestClient client = restClientFactory.create(urlConfig.getUrl());
        client.post(data);
    }

}
