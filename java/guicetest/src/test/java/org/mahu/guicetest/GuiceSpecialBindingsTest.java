package org.mahu.guicetest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Qualifier;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.ConfigurationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;

// This test case explores Annotated bindings: named annotation and (custom qualifier) annotation class.
public class GuiceSpecialBindingsTest {

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Red {
    }

    static class BindingModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(CreditCardProcessor.class).to(RedCreditCardProcessor.class);
            bind(CreditCardProcessor.class).annotatedWith(Names.named("Checkout"))
                    .to(CheckoutCreditCardProcessor.class);
        }
    }

    static interface CreditCardProcessor {
    }

    static class CheckoutCreditCardProcessor implements CreditCardProcessor {
    }

    static class RedCreditCardProcessor implements CreditCardProcessor {
    }

    static class RealBillingService1 {
        private final CreditCardProcessor creditCardProcessor1;
        private final CreditCardProcessor creditCardProcessor2;

        @Inject
        public RealBillingService1(@Named("Checkout") final CreditCardProcessor creditCardProcessor1,
                final CreditCardProcessor creditCardProcessor2) {
            this.creditCardProcessor1 = creditCardProcessor1;
            this.creditCardProcessor2 = creditCardProcessor2;
        }
    }

    static class RealBillingService2 {
        private final CreditCardProcessor creditCardProcessor1;
        private final CreditCardProcessor creditCardProcessor2;
        private final CreditCardProcessor creditCardProcessor3;

        @Inject
        public RealBillingService2(@Named("Checkout") final CreditCardProcessor creditCardProcessor1,
                final CreditCardProcessor creditCardProcessor2,
                @Named("Red") final CreditCardProcessor creditCardProcessor3) {
            this.creditCardProcessor1 = creditCardProcessor1;
            this.creditCardProcessor2 = creditCardProcessor2;
            this.creditCardProcessor3 = creditCardProcessor3;
        }
    }

    @Test
    public void bindWithAndWithAnnotation() throws Exception {
        Injector injector = Guice.createInjector(new BindingModule());
        RealBillingService1 billingService = injector.getInstance(RealBillingService1.class);

        assertNotNull(billingService.creditCardProcessor1);
        assertNotNull(billingService.creditCardProcessor2);
    }

    @Test
    public void bindWithAndWithAnnotationUsingChild() throws Exception {
        Injector injector = Guice.createInjector(new BindingModule());
        Injector childInjector = injector.createChildInjector(new AbstractModule() {

            @Override
            protected void configure() {
                bind(CreditCardProcessor.class).annotatedWith(Names.named("Red")).to(CheckoutCreditCardProcessor.class);
                /**
                 * next bind(..) is not allowed, because it exists in parent.
                 * 
                 * bind(CreditCardProcessor.class).annotatedWith(Names.named("Checkout"))
                 * .to(CheckoutCreditCardProcessor.class);
                 */
            }

        });

        RealBillingService2 billingService = childInjector.getInstance(RealBillingService2.class);

        assertNotNull(billingService.creditCardProcessor1);
        assertNotNull(billingService.creditCardProcessor2);
        assertNotNull(billingService.creditCardProcessor3);

        try {
            injector.getInstance(RealBillingService2.class);
            fail("RealBillingService2 contains a dependency that can only be resolved via top-level inject");
        } catch (ConfigurationException e) {
            // @formatter:off
            /*
             com.google.inject.ConfigurationException: Guice configuration errors:
             1) Unable to create binding for org.mahu.guicetest.GuiceSpecialBindingsTest$RealBillingService2. 
                It was already configured on one or more child injectors or private modules
                      (bound by a just-in-time binding)
                If it was in a PrivateModule, did you forget to expose the binding?
                while locating org.mahu.guicetest.GuiceSpecialBindingsTest$RealBillingService2
             */
            // @formatter:on            
        }
    }

}
