package org.mahu.proto.webguice.inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import javax.inject.Inject;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.servlet.ServletScopes;

@RunWith(Enclosed.class)
public class RequestScopedExecutorTest {

    @Ignore
    static class ParentModule extends AbstractModule {
        @Override
        protected void configure() {
            // Next line will result in creation of 1 RequestData object for a
            // RequestScope.
            bind(GlobalData.class);
        }
    }

    @Ignore
    static class ChildModule extends AbstractModule {
        @Override
        protected void configure() {
            // Next line will result in creation of 1 RequestData object for a
            // RequestScope.
            bind(RequestData.class).in(ServletScopes.REQUEST);
        }

        @Provides
        Data provideData(RequestData requestData) {
            return requestData.data;
        }
    }

    @Ignore
    public static class GlobalData {
    }

    @Ignore
    public static class Data {
    }

    @Ignore
    public static class RequestData {
        private final Data data = new Data();
    }

    @Ignore
    static class TestObject1 {
        final RequestData requestData;
        final Data data;

        @Inject
        TestObject1(final RequestData requestData, final Data data) {
            this.requestData = requestData;
            this.data = data;
        }
    }

    @Ignore
    static class TestObject2 {
        final RequestData requestData;
        final Data data;
        final GlobalData globalData;

        @Inject
        TestObject2(final RequestData requestData, final Data data, final GlobalData globalData) {
            this.requestData = requestData;
            this.data = data;
            this.globalData = globalData;
        }
    }

    @RunWith(Parameterized.class)
    public static class RequestScopedExecutorTest1 {

        @Parameters
        public static Injector[] data() {
            return new Injector[] { Guice.createInjector(new ChildModule()),
                    Guice.createInjector(new ParentModule()).createChildInjector(new ChildModule()) };
        }

        private final Injector injector;

        public RequestScopedExecutorTest1(final Injector injector) {
            this.injector = injector;
        }

        @Test
        public void execute_inScopeSameObjectsAreCreated() throws Exception {
            // Type of injector is set in ctor
            RequestScopedExecutor.execute(new RequestScopeRunnable<TestObject1>() {
                public TestObject1 run() {
                    TestObject1 testObject1 = injector.getInstance(TestObject1.class);
                    TestObject1 testObject2 = injector.getInstance(TestObject1.class);

                    assertEquals(testObject1.requestData, testObject2.requestData);
                    assertEquals(testObject1.data, testObject2.data);

                    return testObject1;
                }
            });
        }

        @Test
        public void execute_inDifferentScopesDifferentObjectsAreCreated() throws Exception {
            // Type of injector is set in ctor
            TestObject1 testObject1 = RequestScopedExecutor.execute(new RequestScopeRunnable<TestObject1>() {
                public TestObject1 run() {
                    return injector.getInstance(TestObject1.class);
                }
            });

            TestObject1 testObject2 = RequestScopedExecutor.execute(new RequestScopeRunnable<TestObject1>() {
                public TestObject1 run() {
                    return injector.getInstance(TestObject1.class);
                }
            });

            assertNotEquals(testObject1.requestData, testObject2.requestData);
            assertNotEquals(testObject1.data, testObject2.data);
        }
    }

    public static class RequestScopedExecutorTest2 {

        @Test
        public void execute_createChildInjector_inDifferentScopesDifferentObjectsAreCreated() throws Exception {
            Injector injector = Guice.createInjector(new ParentModule());

            TestObject2[] testObject = new TestObject2[2];
            for (int i = 0; i < testObject.length; i++) {
                testObject[i] = RequestScopedExecutor.execute(new RequestScopeRunnable<TestObject2>() {
                    public TestObject2 run() {
                        final Injector childInjector = injector.createChildInjector(new ChildModule());
                        return childInjector.getInstance(TestObject2.class);
                    }
                });
            }

            assertNotEquals(testObject[0].requestData, testObject[1].requestData);
            assertNotEquals(testObject[0].data, testObject[1].data);
        }
    }

}
