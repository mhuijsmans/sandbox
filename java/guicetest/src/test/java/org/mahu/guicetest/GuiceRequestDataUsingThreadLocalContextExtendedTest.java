package org.mahu.guicetest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;

import javax.inject.Inject;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.ProvisionException;
import com.google.inject.assistedinject.FactoryModuleBuilder;

// This test case is about making request specific data available for injection using a ThreadLocal context.
// To prevent memory leaks, thread local data must be cleared.

public class GuiceRequestDataUsingThreadLocalContextExtendedTest {

    private static final String IMPL_ERROR = "ImplError";

    static abstract class DataContentIdentifier<T> {

        private final String name;

        protected DataContentIdentifier(final String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            return name.equals(((DataContentIdentifier<?>) obj).name);
        }

    }

    static final class RealDataContentIdentifier<T> extends DataContentIdentifier<T> {
        RealDataContentIdentifier(final String name) {
            super(name);
        }

        // @formatter:off
        public static final RealDataContentIdentifier<IRequestData> REQUEST_DATA =
                new RealDataContentIdentifier<>("RequestData");
        public static final RealDataContentIdentifier<IReport> REPORT =
                new RealDataContentIdentifier<>("Report");        
        // @formatter:on           
    }

    /**
     * See ApiDataContext.java
     */
    static class ThreadLocalContext {
        private final HashMap<DataContentIdentifier<?>, Object> data = new HashMap<>();

        public <T> void put(final DataContentIdentifier<T> identifier, final T value) {
            data.put(identifier, value);
        }

        @SuppressWarnings("unchecked")
        public <T> T get(final DataContentIdentifier<T> identifier) {
            return (T) data.get(identifier);
        }

        public <T> boolean contains(final DataContentIdentifier<T> identifier) {
            return data.containsKey(identifier);
        }

    }

    static interface IThreadLocalData {

        ThreadLocalContext getThreadLocalContext();

        void setThreadLocalContext(ThreadLocalContext context);

        void clear();

    }

    /**
     * This class holds the ThreadLocal specific data. Java guarantees that each
     * thread has an own instance of ThreadLocal and therefore it can hold
     * request specific data.
     * 
     * Note that if another thread is created, threadLOcal data in not available
     * in the other thread.
     */
    static class ThreadLocalData implements IThreadLocalData {

        private final ThreadLocal<ThreadLocalContext> myThreadLocal = new ThreadLocal<ThreadLocalContext>() {
            @Override
            protected ThreadLocalContext initialValue() {
                // If get() is called and no value is set initialValue() is
                // returned. Throw exception, because ThreadLocalContext shall
                // be set.
                throw new RuntimeException(IMPL_ERROR);
            }
        };

        public ThreadLocalContext getThreadLocalContext() {
            return myThreadLocal.get();
        }

        public void setThreadLocalContext(ThreadLocalContext context) {
            myThreadLocal.set(context);
        }

        public void clear() {
            myThreadLocal.remove();
        }

    }

    public static interface IRequestData {
    }

    public static class RequestData implements IRequestData {
    }

    public static interface IReport {
    }

    public static class Report implements IReport {
    }

    public interface ReportFactory {
        public IReport createReport();
    }

    static interface IRequest1 {

    }

    static class Request1 implements IRequest1 {
        final IRequestData requestData;
        final IReport report;

        @Inject
        Request1(final IRequestData requestData, final IReport report) {
            this.requestData = requestData;
            this.report = report;
        }

    }

    static interface IRequest2 {

    }

    static class Request2 implements IRequest2 {
        final IReport report;

        @Inject
        Request2(final IReport report) {
            this.report = report;
        }

    }

    // =================================================================

    static final class GlobalBindingModule extends AbstractModule {
        @Override
        protected void configure() {
            install(new RequestModule1());
            install(new RequestModule2());

            // Thread local data is defined here for all private modules.
            // Because it is thread local, it can be defined in this module.
            bind(IThreadLocalData.class).toInstance(new ThreadLocalData());
        }

    }

    static final class RequestModule1 extends PrivateModule {

        @Override
        protected void configure() {
            bind(IRequest1.class).to(Request1.class);
            // IReport is request specific data, i.e. a single instance exists
            // for a request.
            // A Guice factory needs to be defined so that creation of the
            // object is done by Guice and can make use of injection.
            install(new FactoryModuleBuilder().implement(IReport.class, Report.class).build(ReportFactory.class));

            expose(IRequest1.class);
        }

        /**
         * Make IRequestData (request specific data) available for injection.
         * Request specific data is stored as ThreadLocal data. The actual
         * object is set by as TLD prior to calling
         */
        @Provides
        IRequestData provideRequestData(IThreadLocalData tld) {
            return tld.getThreadLocalContext().get(RealDataContentIdentifier.REQUEST_DATA);
        }

        @Provides
        IReport provideReport(IThreadLocalData tld, ReportFactory reportFactory) {
            IReport report = tld.getThreadLocalContext().get(RealDataContentIdentifier.REPORT);
            if (report == null) {
                report = reportFactory.createReport();
                tld.getThreadLocalContext().put(RealDataContentIdentifier.REPORT, report);
            }
            return report;
        }

    }

    static final class RequestModule2 extends PrivateModule {

        @Override
        protected void configure() {
            bind(IRequest2.class).to(Request2.class);

            expose(IRequest2.class);
        }

        @Provides
        IReport provideReport(IThreadLocalData tld) {
            // Look at RequestModule1 for correct implementation of this
            // provides.
            return tld.getThreadLocalContext().get(RealDataContentIdentifier.REPORT);
        }

    }

    // =================================================================

    @Test
    public void requestModule1() throws Exception {
        Injector injector = Guice.createInjector(new GlobalBindingModule());

        /**
         * Required pattern for use of ThreadLocalData, set data and clear when
         * done.
         */
        final IThreadLocalData tld = injector.getInstance(IThreadLocalData.class);
        try {
            final ThreadLocalContext tlc = new ThreadLocalContext();
            tlc.put(RealDataContentIdentifier.REQUEST_DATA, new RequestData());
            tld.setThreadLocalContext(tlc);

            assertFalse(tlc.contains(RealDataContentIdentifier.REPORT));

            // TLD now contain data that is available for usage.
            injector.getInstance(IRequest1.class);

            assertTrue(tlc.contains(RealDataContentIdentifier.REPORT));
        } finally {
            // Clear is required
            tld.clear();
        }
    }

    @Test
    public void requestModule1_usingLambdaHelper() throws Exception {
        Injector injector = Guice.createInjector(new GlobalBindingModule());

        withThreadLocalData(injector, tld -> {
            final ThreadLocalContext tlc = new ThreadLocalContext();
            tlc.put(RealDataContentIdentifier.REQUEST_DATA, new RequestData());
            tld.setThreadLocalContext(tlc);

            // RequestModule1 shall set Report
            assertFalse(tlc.contains(RealDataContentIdentifier.REPORT));

            // TLD now contain data that is available for usage.
            injector.getInstance(IRequest1.class);

            assertTrue(tlc.contains(RealDataContentIdentifier.REPORT));
        });
    }

    @Test
    public void requestModule1_loop() throws Exception {
        Injector injector = Guice.createInjector(new GlobalBindingModule());

        for (int i = 0; i < 3; i++) {
            withThreadLocalData(injector, tld -> {
                final ThreadLocalContext tlc = new ThreadLocalContext();
                tlc.put(RealDataContentIdentifier.REQUEST_DATA, new RequestData());
                tld.setThreadLocalContext(tlc);

                // RequestModule1 shall set Report
                assertFalse(tlc.contains(RealDataContentIdentifier.REPORT));

                // TLD now contains data that is available for injection.
                injector.getInstance(IRequest1.class);

                assertTrue(tlc.contains(RealDataContentIdentifier.REPORT));
            });
        }
    }

    @Test
    public void requestModule1_afterCallThreadLocalContextIsDeleted() throws Exception {
        // given
        Injector injector = Guice.createInjector(new GlobalBindingModule());
        for (int i = 0; i < 3; i++) {
            withThreadLocalData(injector, tld -> {
                final ThreadLocalContext tlc = new ThreadLocalContext();
                tlc.put(RealDataContentIdentifier.REQUEST_DATA, new RequestData());
                tld.setThreadLocalContext(tlc);
                // TLD now contains data that is available for injection.
                injector.getInstance(IRequest1.class);
            });
        }

        // when
        try {
            injector.getInstance(IThreadLocalData.class).getThreadLocalContext();

            // then
            fail("After withThreadLocalData ThreadLocalContext is deleted, so exception excepted");
        } catch (RuntimeException e) {
            assertEquals(IMPL_ERROR, e.getMessage());
        }
    }

    @Test
    public void requestModule2_dataNotSet() throws Exception {
        Injector injector = Guice.createInjector(new GlobalBindingModule());

        withThreadLocalData(injector, tld -> {
            try {
                final ThreadLocalContext tlc = new ThreadLocalContext();
                tld.setThreadLocalContext(tlc);
                // TLD now contain data that is available for usage.
                injector.getInstance(IRequest2.class);
                fail("Data not set in TLC, so exception expected");
            } catch (ProvisionException e) {
                // com.google.inject.ProvisionException: Unable to provision,
                // see
                // the following errors:
                //
                // 1) null returned by binding at
                // org.mahu.guicetest.GuiceRequestDataUsingThreadLocalContextExtendedTest$RequestModule2.provideReport()
                // but the 1st parameter of
                // org.mahu.guicetest.GuiceRequestDataUsingThreadLocalContextExtendedTest$Request2.<init>(GuiceRequestDataUsingThreadLocalContextExtendedTest.java:199)
                // is not @Nullable
                // while locating
                // org.mahu.guicetest.GuiceRequestDataUsingThreadLocalContextExtendedTest$IReport
                // for the 1st parameter of
                // org.mahu.guicetest.GuiceRequestDataUsingThreadLocalContextExtendedTest$Request2.<init>(GuiceRequestDataUsingThreadLocalContextExtendedTest.java:199)
                // while locating
                // org.mahu.guicetest.GuiceRequestDataUsingThreadLocalContextExtendedTest$Request2
            }
        });
    }

    @Test
    public void requestModule2_tlcNotSet() throws Exception {
        Injector injector = Guice.createInjector(new GlobalBindingModule());

        try {
            injector.getInstance(IRequest2.class);
            fail("Data not set in TLC, so exception expected");
        } catch (ProvisionException e) {
            // com.google.inject.ProvisionException: Unable to provision, see
            // the following errors:
            //
            // 1) Error in custom provider, java.lang.RuntimeException
            // at
            // org.mahu.guicetest.GuiceRequestDataUsingThreadLocalContextExtendedTest$RequestModule2.provideReport(GuiceRequestDataUsingThreadLocalContextExtendedTest.java:158)
            // (via modules:
            // org.mahu.guicetest.GuiceRequestDataUsingThreadLocalContextExtendedTest$GlobalBindingModule
            // ->
            // org.mahu.guicetest.GuiceRequestDataUsingThreadLocalContextExtendedTest$RequestModule2)
            // while locating
            // org.mahu.guicetest.GuiceRequestDataUsingThreadLocalContextExtendedTest$IReport
            // for the 1st parameter of
            // org.mahu.guicetest.GuiceRequestDataUsingThreadLocalContextExtendedTest$Request2.<init>(GuiceRequestDataUsingThreadLocalContextExtendedTest.java:199)
            // while locating
            // org.mahu.guicetest.GuiceRequestDataUsingThreadLocalContextExtendedTest$Request2
            // while locating
            // org.mahu.guicetest.GuiceRequestDataUsingThreadLocalContextExtendedTest$IRequest2
        }
    }

    // =================================================================

    interface ThreadLocalDataUser {
        void execute(final IThreadLocalData tld);
    }

    static void withThreadLocalData(final Injector injector, ThreadLocalDataUser user) {
        /**
         * Required pattern for use of TLD, set data and clear when no done.
         */
        final IThreadLocalData tld = injector.getInstance(IThreadLocalData.class);
        try {
            user.execute(tld);
        } finally {
            tld.clear();
        }
    }

}
