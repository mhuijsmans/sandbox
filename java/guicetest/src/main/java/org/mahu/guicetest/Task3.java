package org.mahu.guicetest;

import javax.inject.Inject;
import javax.inject.Provider;

public class Task3 {
    private final IDiagnosticsLogger diagnosticsLogger;
    private final IConfigData configData;
    private final DataType1 dataType1;
    private final Provider<DataType1> dataType1Provider;

    @Inject
    public Task3(final IDiagnosticsLogger diagnosticsLogger, final IConfigData configData, final DataType1 dataType1,
            final Provider<DataType1> dataType1Provider) {
        this.configData = configData;
        this.dataType1 = dataType1;
        this.dataType1Provider = dataType1Provider;
        this.diagnosticsLogger = diagnosticsLogger;
    }

}
