package org.mahu.guicetest;

import javax.inject.Inject;
import javax.inject.Provider;

public class DataType1Provider implements Provider<DataType1> {
    private final IRequestContext requestContext;

    @Inject
    public DataType1Provider(IRequestContext requestContext) {
        this.requestContext = requestContext;
        System.out.println("DataType1Provider ctor");
    }

    public DataType1 get() {
        return requestContext.get(DataType1.class);
    }
}
