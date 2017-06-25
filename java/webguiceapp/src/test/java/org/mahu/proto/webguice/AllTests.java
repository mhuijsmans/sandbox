package org.mahu.proto.webguice;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.mahu.proto.webguice.request.RequestCommonBindingsModuleTest;
import org.mahu.proto.webguice.rest.RestServiceJerseyTest;
import org.mahu.proto.webguice.rest.RestServiceTest;
import org.mahu.proto.webguice.rest.SimpleTest;
import org.mahu.proto.webguice.stm.IRequestProcessorTest;
import org.mahu.proto.webguice.stm.StateMachineBindingModuleTest;
import org.mahu.proto.webguice.workflowtask.Task2Test;

//@formatter:off
@RunWith(Suite.class)
@Suite.SuiteClasses({ 
    IRequestProcessorTest.class,
    SimpleTest.class,
    RequestCommonBindingsModuleTest.class,
    RestServiceJerseyTest.class,
    RestServiceTest.class,
    StateMachineBindingModuleTest.class,
    Task2Test.class
})
public final class AllTests {
}
//@formatter:on