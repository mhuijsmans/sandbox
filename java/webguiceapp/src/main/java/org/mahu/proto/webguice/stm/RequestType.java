package org.mahu.proto.webguice.stm;

public enum RequestType {

    // @formatter:off
    GET(ExecutionProperty.SHORT), 
    SCAN_END(ExecutionProperty.END), 
    SCAN(ExecutionProperty.LONG, SCAN_END);
    // @formatter:on

    enum ExecutionProperty {
        LONG, SHORT, END;
    };

    private final ExecutionProperty executionProperty;
    private final RequestType endRequestType;

    RequestType(ExecutionProperty executionProperty) {
        this.executionProperty = executionProperty;
        endRequestType = null;
    }

    RequestType(ExecutionProperty executionProperty, RequestType endRequestType) {
        this.executionProperty = executionProperty;
        this.endRequestType = endRequestType;
    }

    public boolean isLongRequest() {
        return executionProperty == ExecutionProperty.LONG;
    }

    public RequestType getEndRequestType() {
        return endRequestType;
    }

}
