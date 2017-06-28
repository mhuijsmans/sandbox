package org.mahu.guicetest;

import java.util.concurrent.TimeUnit;

import javax.inject.Provider;

import org.junit.Test;
import org.mahu.guicetest.util.JavaClassGenerator;

import com.google.common.base.Stopwatch;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

// This test case explores costs associated with use of guice
// - cost of getInstance(BillingService1.class) via guice
// - cost of new BillingService1(..) without guice
// - cost of getInstance(Task3.class) via guice
// - cost of use of a childInjector
// - cost of getInstance(Task3.class) via guice childInjector
public class GuicePerformanceTest {

    @Test
    public void testDifferentCases() {

        Injector injector = Guice.createInjector(new BindingModule1(), new BindingModule2());

        final int max = 2000;
        measure("BillingService via Guice", max, () -> {
            for (int i = 0; i < max; i++) {
                injector.getInstance(BillingService1.class);
            }
        });

        measure("Create instance BillingService", max, () -> {
            for (int i = 0; i < max; i++) {
                ICreditCardProcessor processor = new PaypalCreditCardProcessor();
                BillingService2 billingService2 = new BillingService2(processor);
                new BillingService1(processor, billingService2);
            }
        });

        measure("Task2 via Guice", max, () -> {
            for (int i = 0; i < max; i++) {
                injector.getInstance(Task2.class);
            }
        });

        measure("ChildInjector", max, () -> {
            for (int i = 0; i < max; i++) {
                IDiagnosticsLogger diagnosticsLogger = new DiagnosticsLogger();
                injector.createChildInjector(new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(IDiagnosticsLogger.class).toInstance(diagnosticsLogger);
                    }
                });
            }
        });

        measure("ChildInjector (100 bindings)", max, () -> {
            for (int i = 0; i < max; i++) {
                IDiagnosticsLogger diagnosticsLogger = new DiagnosticsLogger();
                injector.createChildInjector(new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(IDiagnosticsLogger.class).toInstance(diagnosticsLogger);
                        for (int i = 0; i < 100; i++) {
                            try {
                                bind(JavaClassGenerator.generateClass("classname" + i));
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                });
            }
        });

        IDiagnosticsLogger diagnosticsLogger = new DiagnosticsLogger();
        Injector requestInjector = injector.createChildInjector(new AbstractModule() {

            @Override
            protected void configure() {
                bind(IDiagnosticsLogger.class).toInstance(diagnosticsLogger);
                for (int i = 0; i < 100; i++) {
                    try {
                        bind(JavaClassGenerator.generateClass("classname" + i));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        measure("ChildInjector - task3 (x200)", max, () -> {
            for (int i = 0; i < max / 100; i++) {
                for (int j = 0; j < 200; j++) {
                    requestInjector.getInstance(Task3.class);
                }
            }
        });

        measure("Direct- task3 (x200)", max, () -> {
            final IDiagnosticsLogger diagnosticsLogger1 = null;
            final IConfigData configData = null;
            final DataType1 dataType1 = null;
            final Provider<DataType1> dataType1Provider = null;
            for (int i = 0; i < max; i++) {
                for (int j = 0; j < 200; j++) {
                    Task3 task3 = new Task3(diagnosticsLogger1, configData, dataType1, dataType1Provider);
                }
            }
        });

    }

    public static void measure(final String text, final long max, Runnable r) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        r.run();
        stopwatch.stop(); // optional
        long nanos = stopwatch.elapsed(TimeUnit.NANOSECONDS);
        System.out.println("##### " + text);
        long avgNano = nanos / max;
        long avgMicro = nanos / (max * 1000);
        long avgMilli = nanos / (max * 1000 * 1000);
        System.out.println("max=" + max);
        System.out.println("elapsed(nanosec)=" + nanos);
        System.out.println("avg(nanosec)=" + avgNano);
        System.out.println("avg(microsec)=" + avgMicro);
        System.out.println("avg(millisec)=" + avgMilli);
    }
}
