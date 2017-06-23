package org.mahu.proto.webguice;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.mahu.proto.webguice.rest.RestServiceTest;
import org.mahu.proto.webguice.stm.IRequestProcessorTest;
import org.mahu.proto.webguice.stm.StateMachineBindingModuleTest;

//@formatter:off
@RunWith(Suite.class)
@Suite.SuiteClasses({ 
    IRequestProcessorTest.class   ,  
    RestServiceTest.class, 
    StateMachineBindingModuleTest.class
})
public final class AllTests {
}
//@formatter:on