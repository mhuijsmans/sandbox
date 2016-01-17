package org.mahu.proto.lifecycle;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mahu.proto.lifecycle.example2.ModuleBindings2;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class ModuleBindings2Test {

    private Injector injector;
    private ModuleBindings2 moduleBindings;

    @Before
    public void initGuice() {
        moduleBindings = new ModuleBindings2();
    }

    @After
    public void freeTestData() {
        injector = null;
    }

    @Test
    public void createLifeCycleServicesObjects_allObjectsAreCreated() {
        moduleBindings = new ModuleBindings2();
        injector = Guice.createInjector(moduleBindings);
        moduleBindings.createLifeCycleServicesObjects(injector);
        
        assertEquals(9, moduleBindings.getObjectRegistry().getObjectCount());
    }

}
