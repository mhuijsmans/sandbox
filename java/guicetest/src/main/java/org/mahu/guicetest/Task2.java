package org.mahu.guicetest;

import javax.inject.Inject;

public class Task2 {
    private final IRequestScanResultData iRequestScanResultData;
    private final RequestResultData requestScanResultData;

    @Inject
    public Task2(final IRequestScanResultData iRequestScanResultData,
            final RequestResultData requestScanResultData) {
        this.iRequestScanResultData = iRequestScanResultData;
        this.requestScanResultData = requestScanResultData;
    }

    public void act() {
        System.out.println("Task, is IRequestScanResultData injected: " + (iRequestScanResultData != null));
        System.out.println("Task, is RequestScanResultData injected: " + (requestScanResultData != null));
    }
}
