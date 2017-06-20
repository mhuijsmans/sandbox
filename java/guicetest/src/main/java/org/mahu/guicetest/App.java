package org.mahu.guicetest;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class App {
    public static void main(String[] args) {

        /**
         * DIContainer holds the Guice injector. That can be used to build
         * objects where Guice will do injection
         */
        Injector injector = Guice.createInjector(new BindingModule1(), new BindingModule2());
        try {
            // next line created a cyclic dependency. That is (bad but needed)
            // and fixed in finally
            injector.getInstance(InjectorProxy.class).set(injector);

            BillingService1 billingService = injector.getInstance(BillingService1.class);
            billingService.chargeOrder(10);

            // RequestResultData makes data from currently executing / latest
            // executed request available as global data.
            // When a new request is accepted that impacts the
            // RequestResultData, the RequestResultData object will be cleared.
            RequestResultData requestResultData = injector.getInstance(RequestResultData.class);
            requestResultData.clear();
            requestResultData.set(new DataType2());

            // Create a child Injector with bindings specific for the request.
            // Binding can differ per request
            IDiagnosticsLogger diagnosticsLogger = new DiagnosticsLogger();
            Injector requestInjector = injector.createChildInjector(new AbstractModule() {
                @Override
                protected void configure() {
                    bind(IDiagnosticsLogger.class).toInstance(diagnosticsLogger);
                }
            });
            requestInjector.getInstance(Task3.class);

        } finally {
            injector.getInstance(InjectorProxy.class).set(null);
        }
    }
}
