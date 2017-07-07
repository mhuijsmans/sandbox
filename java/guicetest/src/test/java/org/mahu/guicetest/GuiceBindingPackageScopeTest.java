package org.mahu.guicetest;

import javax.inject.Inject;

import org.junit.Test;
import org.mahu.guicetest.test1.ITestClass;
import org.mahu.guicetest.test1.PublicTestClass;
import org.mahu.guicetest.test1.Test1Module;

import com.google.inject.Guice;
import com.google.inject.Injector;

// This test case explores binding and package scope. 
// Conclusion is that it is important that 
public class GuiceBindingPackageScopeTest {

    static class TestObject1 {

        private final ITestClass testclass;

        @Inject
        TestObject1(ITestClass testclass) {
            this.testclass = testclass;
        }
    }

    // PublicTestClass is defined as public so it can be injected BY HUMAN ERROR
    // User should have used: ITestClass
    static class TestObject2 {

        private final PublicTestClass testclass;

        @Inject
        TestObject2(PublicTestClass testclass) {
            this.testclass = testclass;
        }
    }

    // @formatter:off
    /*
    TestObject3 can not be defined because PackageScopeTestClass is not visible here.
    But it is visible in the Test1Module.
    static class TestObject3 {

        private final PackageScopeTestClass testclass;

        @Inject
        TestObject3(PackageScopeTestClass testclass) {
            this.testclass = testclass;
        }
    }
    */
    // @formatter:on    

    @Test
    public void testBasicBinding() throws Exception {
        Injector injector = Guice.createInjector(new Test1Module());
        TestObject1 to1 = injector.getInstance(TestObject1.class);

        System.out.println(to1.testclass.getClass().getName());

        TestObject2 to2 = injector.getInstance(TestObject2.class);
        System.out.println(to2.testclass.getClass().getName());
    }

}
