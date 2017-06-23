package org.mahu.proto.webguice.stm;

import org.junit.Test;

import com.google.inject.Guice;

public class StateMachineBindingModuleTest {

    @Test
    public void bindingWorks() {
        Guice.createInjector(new StateMachineBindingModule());
    }

}
