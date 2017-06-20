package org.mahu.guicetest;

import com.google.inject.AbstractModule;

public class BindingModule2 extends AbstractModule {
    @Override
    protected void configure() {
        RequestResultData requestScanResultData = new RequestResultData();
        bind(IRequestScanResultData.class).toInstance(requestScanResultData);
        bind(RequestResultData.class).toInstance(requestScanResultData);
    }
}
