package org.mahu.proto.webguice.scanworkflow;

import org.mahu.proto.webguice.annotation.NotDefinedBindings;

import com.google.inject.AbstractModule;

// @formatter:off
@NotDefinedBindings(binding = { 
        ScanWorkFlowSettings.class // Defines data requires by the  ScanWorkFlow
        })
//@formatter:on
public class ScanWorkflowModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ScanWorkFlow.class);
    }

    // This class can contain provider methods that take ScanWorkFlowSettings as
    // input and generate Objects for injection into the ScanWorkFlow.

}
