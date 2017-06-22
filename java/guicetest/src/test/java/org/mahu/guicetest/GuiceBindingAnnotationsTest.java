package org.mahu.guicetest;

import static org.junit.Assert.assertNotNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Qualifier;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;

// This test case explores Annotated bindings: named annotation and (custom qualifier) annotation class.
public class GuiceBindingAnnotationsTest {

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Red {
    }

    static class BindingModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(CreditCardProcessor.class).annotatedWith(Names.named("Checkout"))
                    .to(CheckoutCreditCardProcessor.class);
            bind(CreditCardProcessor.class).annotatedWith(Red.class).to(RedCreditCardProcessor.class);
        }
    }

    static interface CreditCardProcessor {
    }

    static class CheckoutCreditCardProcessor implements CreditCardProcessor {
    }

    static class RedCreditCardProcessor implements CreditCardProcessor {
    }

    static class RealBillingService {
        private final CreditCardProcessor creditCardProcessor1;
        private final CreditCardProcessor creditCardProcessor2;

        @Inject
        public RealBillingService(@Named("Checkout") final CreditCardProcessor creditCardProcessor1,
                @Red final CreditCardProcessor creditCardProcessor2) {
            this.creditCardProcessor1 = creditCardProcessor1;
            this.creditCardProcessor2 = creditCardProcessor2;
        }
    }

    @Test
    public void requestScope() throws Exception {
        Injector injector = Guice.createInjector(new BindingModule());
        RealBillingService billingService = injector.getInstance(RealBillingService.class);

        assertNotNull(billingService.creditCardProcessor1);
        assertNotNull(billingService.creditCardProcessor2);
    }

}
