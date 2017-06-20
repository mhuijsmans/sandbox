package org.mahu.guicetest;

import java.util.Optional;

// RequestResultData holds the requestResult data available upto start of next request.
public class RequestResultData implements IRequestScanResultData {

    Optional<DataType2> dataType2 = Optional.empty();

    public RequestResultData() {
        System.out.println("RequestResultData ctor()");
    }

    public void clear() {
        dataType2 = Optional.empty();
    }

    public void set(DataType2 dataType2) {
        this.dataType2 = Optional.of(dataType2);
    }

    @Override
    public Optional<DataType2> getDataType2() {
        return dataType2;
    }

}
