package org.mahu.guicetest;

import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.google.inject.assistedinject.FactoryModuleBuilder;

// This test case explores FactoryModuleBuilder.
// Source: https://google.github.io/guice/api-docs/4.1/javadoc/com/google/inject/assistedinject/FactoryModuleBuilder.html
public class GuiceFactoryModuleBuilderBindingTest {

    static class BindingModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(CreditService.class).to(CreditCardCreditService.class);
            bind(Payment.class).to(RealPayment1.class);
            install(new FactoryModuleBuilder().implement(Payment.class, RealPayment1.class)
                    .build(PaymentFactory1.class));
            install(new FactoryModuleBuilder().implement(Payment.class, RealPayment2.class)
                    .build(PaymentFactory2.class));
        }
    }

    public interface PaymentFactory1 {
        Payment create();
    }

    public interface PaymentFactory2 {
        // Different methods must have difference names
        Payment createWithMoney(Money money);

        Payment createWithMoneyAndDate(Money money, Date date);
    }

    public static interface Payment {
    }

    public static interface CreditService {
    }

    public static class CreditCardCreditService implements CreditService {
    }

    public static class Money {
    }

    public static class Date {
    }

    public static class RealPayment1 implements Payment {
        private final CreditService creditService;

        @Inject
        public RealPayment1(CreditService creditService) {
            this.creditService = creditService;
        }

    }

    public static class RealPayment2 implements Payment {
        private final CreditService creditService;
        private final Money money;
        private final Date date;

        @AssistedInject
        public RealPayment2(CreditService creditService, @Assisted Money money) {
            this.creditService = creditService;
            this.money = money;
            this.date = null;
        }

        @AssistedInject
        public RealPayment2(CreditService creditService, @Assisted Money money, @Assisted Date date) {
            this.creditService = creditService;
            this.money = money;
            this.date = date;
        }
    }

    @Test
    public void requestScope() throws Exception {
        Injector injector = Guice.createInjector(new BindingModule());

        PaymentFactory1 paymentFactory1 = injector.getInstance(PaymentFactory1.class);
        assertTrue(paymentFactory1.create() instanceof RealPayment1);

        PaymentFactory2 paymentFactory2 = injector.getInstance(PaymentFactory2.class);
        assertTrue(paymentFactory2.createWithMoney(new Money()) instanceof RealPayment2);
        assertTrue(paymentFactory2.createWithMoneyAndDate(new Money(), new Date()) instanceof RealPayment2);
    }

}
