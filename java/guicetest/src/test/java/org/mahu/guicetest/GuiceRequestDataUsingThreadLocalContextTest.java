package org.mahu.guicetest;

import javax.inject.Inject;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;

// This test case is about making request specific data available for injection using a ThreadLocal context.
// To prevent memory leaks, thread local data must be cleared.
public class GuiceRequestDataUsingThreadLocalContextTest {

    /**
     * ThreadLocalContext holds all thread specific data that is available for
     * injection. In the example below, it is a single class.
     */
    static class ThreadLocalContext {
        private final RequestData o;

        ThreadLocalContext(final RequestData o) {
            this.o = o;
        }

        public RequestData getRequestData() {
            return o;
        }

    }

    /**
     * This class holds the ThreadLocal specific data. Java guarantees that each
     * thread has an own instance of ThreadLocal and therefore it can hold
     * request specific data.
     * 
     * Note that if another thread is created, threadLOcal data in not available
     * in the other thread.
     */
    static class ThreadLocalData {
        private static final ThreadLocal<ThreadLocalContext> myThreadLocal = new ThreadLocal<ThreadLocalContext>() {
            @Override
            protected ThreadLocalContext initialValue() {
                // If get() is called and no value is set initialValue() is
                // returned.
                throw new RuntimeException();
            }
        };

        ThreadLocalContext getThreadLocalContext() {
            return myThreadLocal.get();
        }

        void setThreadLocalContext(ThreadLocalContext context) {
            myThreadLocal.set(context);
        }

        void clear() {
            myThreadLocal.remove();
        }

    }

    static class GlobalBindingModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(ThreadLocalData.class).toInstance(new ThreadLocalData());
        }

        /**
         * This @provides make the request specific data available for
         * injection.
         */
        @Provides
        RequestData provideRequestData(ThreadLocalData tld) {
            return tld.getThreadLocalContext().getRequestData();
        }
    }

    public static class RequestData {
    }

    static class Request {
        final RequestData requestData;

        @Inject
        Request(final RequestData requestData) {
            this.requestData = requestData;
        }

    }

    @Test
    public void requestScope() throws Exception {
        Injector injector = Guice.createInjector(new GlobalBindingModule());

        /**
         * Required pattern for use of TLD, set data and clear when no done.
         */
        final ThreadLocalData tld = injector.getInstance(ThreadLocalData.class);
        try {
            final ThreadLocalContext tlc = new ThreadLocalContext(new RequestData());
            tld.setThreadLocalContext(tlc);
            // TLD now contain data that is available for usage.
            injector.getInstance(Request.class);
        } finally {
            tld.clear();
        }
    }

}
