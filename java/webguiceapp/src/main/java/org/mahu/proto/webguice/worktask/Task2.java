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
    private final TaskResultData<Task2ResultData> task2ResultData;

    @AssistedInject
    Task2(final UrlConfig urlConfig, final IRestClientFactory restClientFactory, @Assisted Task2Data data,
            @Assisted final TaskResultData<Task2ResultData> task2ResultData) {
        this.data = data;
        this.urlConfig = urlConfig;
        this.restClientFactory = restClientFactory;
        this.task2ResultData = task2ResultData;
    }

    public void execute() {
        IRestClient client = restClientFactory.create(urlConfig.getUrl());
        client.post(data);
        task2ResultData.set(new Task2ResultData());
    }

}
