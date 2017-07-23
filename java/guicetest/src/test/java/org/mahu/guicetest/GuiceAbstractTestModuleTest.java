package org.mahu.guicetest;

import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;

// This test case explores use of an own module.
// Overriding @provides is considered harmful. Thus make @provides final  
// https://groups.google.com/forum/#!msg/google-guice/bRo5SvmzpdI/HQ0Y12RZR1QJ
// Also the methods defined in the base class are harmful.
public class GuiceAbstractTestModuleTest {

    static abstract class AbstractTestModule extends AbstractModule {

        @Provides
        final IData1 providesData1() {
            return createData1();
        }

        @Provides
        final IData2 providesData2() {
            return createData2();
        }

        abstract IData1 createData1();

        IData2 createData2() {
            return new Data2a();
        }

    }

    static class TestModule1 extends AbstractTestModule {
        @Override
        protected void configure() {
        }

        IData1 createData1() {
            return new Data1();
        }

        IData2 createData2() {
            return new Data2b();
        }

    }

    static interface IData1 {
    }

    static interface IData2 {
    }

    static class Data1 implements IData1 {
    }

    static class Data2a implements IData2 {
    }

    static class Data2b implements IData2 {
    }

    static class TestObject {

        private IData1 data1;
        private IData2 data2;

        @Inject
        TestObject(IData1 data1, IData2 data2) {
            this.data1 = data1;
            this.data2 = data2;
        }
    }

    @Test
    public void testBasicBinding() throws Exception {
        Injector injector = Guice.createInjector(new TestModule1());
        TestObject testObject = injector.getInstance(TestObject.class);

        assertTrue(testObject.data1 instanceof Data1);
        assertTrue(testObject.data2 instanceof Data2b);
    }

}
