package org.mahu.proto.lifecycle.example2;

public interface IErrorRequest {
    
    public static final String RESPONSE = "response";
    public static final String RESPONSE_OK = "responseOk";
    public static final String THROW_EXCEPTION_MSG = "THROW_EXCEPTION_MSG";
    public final static String MSG_EXCEPTION_IN_STOP_SERVICE = "ErrorService.ExceptionInStopService"; 

    public static enum Test {
        throwException, causeUncaughtException, causeTwoUncaughtExceptions, throwExceptionInStopService
    };

    String process(Test data);

}
