package org.mahu.guicetest;

import javax.inject.Inject;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;

// This test case is about making request specific data available for injection.
// It makes use of a childInjector. 
public class GuiceRequestDataTest {

    static class GlobalBindingModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(GlobalData.class).in(Singleton.class);
        }
    }

    public static class GlobalData {
    }

    static class RequestBindingModule extends AbstractModule {
        private final RequestData requestData;

        public RequestBindingModule(final RequestData requestData) {
            this.requestData = requestData;
        }

        @Override
        protected void configure() {
            bind(Request.class);
            bind(RequestData.class).toInstance(requestData);
        }
    }

    public static class RequestData {
    }

    static class Request {
        final GlobalData globalData;
        final RequestData requestData;

        @Inject
        Request(final GlobalData globalData, final RequestData requestData) {
            this.globalData = globalData;
            this.requestData = requestData;
        }

        void execute() {
        }
    }

    @Test
    public void requestScope() throws Exception {
        Injector injector = Guice.createInjector(new GlobalBindingModule());

        RequestData requestData = new RequestData();
        Injector childInjector = injector.createChildInjector(new RequestBindingModule(requestData));

        Request request = childInjector.getInstance(Request.class);

        request.execute();
    }

}
