package org.mahu.proto.lifecycle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mahu.proto.lifecycle.example1.ExampleInterface1;
import org.mahu.proto.lifecycle.example1.ExampleInterface2;
import org.mahu.proto.lifecycle.example1.ExampleInterface3;
import org.mahu.proto.lifecycle.example1.impl.ExampleInterface1Impl;
import org.mahu.proto.lifecycle.example1.impl.ExampleInterface2Impl;
import org.mahu.proto.lifecycle.example1.impl.ModuleBindings1;
import org.mahu.proto.lifecycle.impl.ObjectRegistry;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;

public class GuiceTest {

    private Injector injector;
    private ModuleBindings1 moduleBindings;

    @Before
    public void initGuice() {
        ExampleInterface1Impl.cntr.set(0);
        ExampleInterface2Impl.cntr.set(0);
        moduleBindings = new ModuleBindings1();
        injector = Guice.createInjector(moduleBindings);
    }

    @After
    public void freeTestData() {
        injector = null;
    }

    @Test
    public void getInstance_exampleInterface1_onlyIf1Created() {
        injector.getInstance(ExampleInterface1.class);

        assertEquals(1, ExampleInterface1Impl.cntr.get());
        assertEquals(0, ExampleInterface2Impl.cntr.get());
    }

    @Test
    public void getInstance_exampleInterface2_if1AndIf2Created() {
        injector.getInstance(ExampleInterface2.class);

        assertEquals(1, ExampleInterface1Impl.cntr.get());
        assertEquals(1, ExampleInterface2Impl.cntr.get());
    }

    @Test
    public void getInstance_exampleInterface1And2_if1AndIf2Created() {
        injector.getInstance(ExampleInterface1.class);
        injector.getInstance(ExampleInterface2.class);

        assertEquals(1, ExampleInterface1Impl.cntr.get());
        assertEquals(1, ExampleInterface2Impl.cntr.get());
    }

    @Test
    public void getInstance_exampleInterface2And1_if1AndIf2Created() {
        injector.getInstance(ExampleInterface2.class);
        injector.getInstance(ExampleInterface1.class);

        assertEquals(1, ExampleInterface1Impl.cntr.get());
        assertEquals(1, ExampleInterface2Impl.cntr.get());
    }

    @Test
    public void getInstance_exampleInterface1MultipleTmes_if1CreatedOnce() {
        injector.getInstance(ExampleInterface1.class);
        injector.getInstance(ExampleInterface1.class);

        assertEquals(1, ExampleInterface1Impl.cntr.get());
        assertEquals(0, ExampleInterface2Impl.cntr.get());
    }

    @Test
    public void getInstance_exampleInterface2MultipleTmes_if2CreatedOnce() {
        injector.getInstance(ExampleInterface2.class);
        injector.getInstance(ExampleInterface2.class);

        assertEquals(1, ExampleInterface1Impl.cntr.get());
        assertEquals(1, ExampleInterface2Impl.cntr.get());
    }

    @Test
    public void getInstance_exampleInterface2_creationOrderIs1Next2() {
        injector.getInstance(ExampleInterface2.class);

        ObjectRegistry objectCreationOrder = injector.getInstance(ObjectRegistry.class);

        assertEquals(3, objectCreationOrder.getObjectCount());
        assertTrue(objectCreationOrder.getObject(0) instanceof ExampleInterface1);
        assertTrue(objectCreationOrder.getObject(1) instanceof ExampleInterface3);
        assertTrue(objectCreationOrder.getObject(2) instanceof ExampleInterface2);
    }

    @Test
    public void getInstance_exampleInterface2Twice_creationOrderIs1Next2() {
        injector.getInstance(ExampleInterface2.class);

        ObjectRegistry objectCreationOrder = injector.getInstance(ObjectRegistry.class);

        assertEquals(3, objectCreationOrder.getObjectCount());
        assertTrue(objectCreationOrder.getObject(0) instanceof ExampleInterface1);
        assertTrue(objectCreationOrder.getObject(1) instanceof ExampleInterface3);
        assertTrue(objectCreationOrder.getObject(2) instanceof ExampleInterface2);
    }

    @Test
    public void createLifeCycleServices_2Services_bothAreCreated() {
        injector.getInstance(ExampleInterface2.class);
        moduleBindings.createLifeCycleServicesObjects(injector);

        assertEquals(2, moduleBindings.getObjectRegistry().getLifeCycleServiceCount());
    }

    @Test
    public void createLifeCycleServices_2Services_ObjectsCreatedIs1_2() {
        injector.getInstance(ExampleInterface2.class);
        moduleBindings.createLifeCycleServicesObjects(injector);
        ObjectRegistry objectCreationOrder = moduleBindings.getObjectRegistry();

        assertEquals(3, objectCreationOrder.getObjectCount());
        assertTrue(objectCreationOrder.getObject(0) instanceof ExampleInterface1);
        assertTrue(objectCreationOrder.getObject(1) instanceof ExampleInterface3);
        assertTrue(objectCreationOrder.getObject(2) instanceof ExampleInterface2);
    }

    interface I1 {
    }
    
    static class Impl0 implements I1 {
    }

    static class Impl1 implements I1 {
    }

    class TestAbstractModule extends AbstractModule {

        @Override
        protected void configure() {
            bind(I1.class).to(Impl0.class).in(Scopes.SINGLETON);
        }

        void bindImpl1() {
            bind(I1.class).to(Impl1.class).in(Scopes.SINGLETON);
        }
    }

    @Test
    public void getInstance_configureImpl0_correctClass() {
        TestAbstractModule modules = new TestAbstractModule();
        // The next line will actually result in calling 
        Injector injector = Guice.createInjector(modules);

        assertTrue(injector.getInstance(I1.class) instanceof Impl0);
    }  

    @Test
    public void bindImpl1_afterConfigure_NPE() {
        TestAbstractModule modules = new TestAbstractModule();
        try {
            modules.bindImpl1();
            fail("After module.configure() has been called, content can not be changed anymore.");
        } catch (NullPointerException e) {
            // bindImpl1() calls a bind method; that is not allowed outside configure 
            assertTrue(true);
        }
    }
 
}
