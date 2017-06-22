package org.mahu.guicetest;

import javax.inject.Inject;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;

// This test case explores module hierarchy
// source: http://blog.muhuk.com/2015/05/28/using_guice_effectively.html#.WUvT9-uGOCq
public class GuiceModuleHierarchyTest {

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
            install(new GlobalBindingModule());
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
    }

    @Test
    public void requestScope() throws Exception {
        RequestData requestData = new RequestData();
        Injector childInjector = Guice.createInjector(new RequestBindingModule(requestData));

        childInjector.getInstance(Request.class);
    }

}
