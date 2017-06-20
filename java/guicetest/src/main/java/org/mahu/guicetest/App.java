package org.mahu.guicetest;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class App {
    public static void main(String[] args) {

        Injector injector = Guice.createInjector(new BindingModule1(), new BindingModule2());

        BillingService1 billingService = injector.getInstance(BillingService1.class);
        billingService.chargeOrder(10);

        // RequestResultData makes data from currently executing / latest
        // executed request available as global data.
        // When a new request is accepted that impacts the
        // RequestResultData, the RequestResultData object will be cleared.
        RequestResultData requestResultData = injector.getInstance(RequestResultData.class);
        requestResultData.clear();
        requestResultData.set(new DataType2());
    }
}
