package org.mahu.proto.lifecycle.example2;

public interface IErrorRequest {
    
    public static final String RESPONSE = "response";
    public static final String RESPONSE_OK = "responseOk";
    public static final String THROW_EXCEPTION_MSG = "THROW_EXCEPTION_MSG";

    public static enum Test {
        throwException, causeUncaughtException
    };

    String process(Test data);

}
