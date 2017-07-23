package org.mahu.guicetest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

import javax.inject.Inject;
import javax.inject.Provider;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.BindingAnnotation;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;

// This test case explores AOP: 
// - detected method annotated with @Measurment
// - MethodInterceptor with/without injection
// - MethodInterceptor retrieve annotation property (here name of @Measurement
public class GuiceAopTest {

    // A simple annotation type.
    @Target({ ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    @BindingAnnotation
    @interface Measurement {
        String name();
    }

    static class BindingModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(Paper.class);
            bind(Printer.class);
            bind(TestObject.class);
            {
                // Following defines a generic interceptor (Matcher.any()) that
                // will check any class
                bindInterceptor(Matchers.any(), Matchers.annotatedWith(Measurement.class), new MeasurementLogger1());
            }
            {
                // Following defines a interceptor for TestObject or derived
                final Matcher<Class> classMatcher = Matchers.subclassesOf(TestObject.class);
                final Matcher<AnnotatedElement> methodMatcher = Matchers.annotatedWith(Measurement.class);
                final org.aopalliance.intercept.MethodInterceptor methodInterceptor = new MeasurementLogger2(
                        getProvider(Printer.class));
                bindInterceptor(classMatcher, methodMatcher, methodInterceptor);
            }

            {
                // Following defines a interceptor for TestObject or derived
                bindInterceptor(Matchers.any(), Matchers.any(), new MeasurementLogger3());
            }
        }
    }

    static class Paper {
    }

    static class Printer {

        @Inject
        Printer(Paper paper) {

        }

        void print(String s) {
            System.out.println("Printer: " + s);
        }
    }

    static class TestObject {

        @Inject
        public TestObject() {
        }

        @Measurement(name = "aap")
        public void method1() {
        }

        public void method2() {
        }
    }

    static class MeasurementLogger1 implements MethodInterceptor {
        public Object invoke(MethodInvocation invocation) throws Throwable {
            System.out.println("MeasurementLogger-pre");
            Object obj = invocation.proceed();
            System.out.println("MeasurementLogger-post");
            return obj;
        }
    }

    static class MeasurementLogger2 implements MethodInterceptor {
        private final Provider<Printer> data;

        @Inject
        MeasurementLogger2(Provider<Printer> data) {
            // calling data.get() will result in an error.
            this.data = data;
        }

        public Object invoke(MethodInvocation invocation) throws Throwable {
            Method method = invocation.getMethod();
            Measurement annotation = method.getAnnotation(Measurement.class);

            Printer d = data.get();
            d.print("MeasurementLogger-pre, name=" + annotation.name());
            Object obj = invocation.proceed();
            d.print("MeasurementLogger-post, name=" + annotation.name());
            return obj;
        }
    }

    static class MeasurementLogger3 implements MethodInterceptor {
        public Object invoke(MethodInvocation invocation) throws Throwable {
            System.out.println("####" + invocation.getThis().getClass() + "." + invocation.getMethod().getName());
            Object obj = invocation.proceed();
            return obj;
        }
    }

    @Test
    public void testMethodInterception() throws Exception {
        Injector injector = Guice.createInjector(new BindingModule());
        TestObject to = injector.getInstance(TestObject.class);

        to.method1();
        to.method2();
    }

}
