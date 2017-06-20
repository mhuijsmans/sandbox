package org.mahu.guicetest;

import javax.inject.Inject;
import javax.inject.Provider;

public class Task1 {
    private final IConfigData configData;
    private final DataType1 dataType1;
    private final Provider<DataType1> dataType1Provider;

    @Inject
    public Task1(final IConfigData configData, final DataType1 dataType1, final Provider<DataType1> dataType1Provider) {
        this.configData = configData;
        this.dataType1 = dataType1;
        this.dataType1Provider = dataType1Provider;
    }

    public void act() {
        System.out.println("Task, is configData injected: " + (configData != null));
        System.out.println("Task, is dataType1 injected: " + (dataType1 != null));
        System.out.println("Task, is dataType1Provider injected: " + (dataType1Provider != null));
        System.out.println("Task, configData=" + configData.getValue());
    }
}
