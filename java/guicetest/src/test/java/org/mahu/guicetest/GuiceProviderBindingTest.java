package org.mahu.guicetest;

import static org.junit.Assert.assertNotEquals;

import javax.inject.Inject;
import javax.inject.Provider;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

// This test case explores Provider responsible for generating an object.
// source: https://github.com/google/guice/wiki/ProviderBindings
public class GuiceProviderBindingTest {

    static class BindingModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(RequestData.class).toProvider(RequestDataProvider.class);
            bind(ITestObject1.class).to(TestObject1.class);
        }
    }

    public static class RequestData {
    	private static int globalSeqNr = 0;
    	
    	private int seqNr = globalSeqNr++;
    }

    static class RequestDataProvider implements Provider<RequestData> {

        @Override
        public RequestData get() {
            // New is applied here, so no injection for RequestData
            return new RequestData();
        }

    }
    
    static interface ITestObject1 {
    	
    	public RequestData getRequestData();
    	
    }

    static class TestObject1 implements ITestObject1{
        final RequestData requestData;

        @Inject
        TestObject1(final RequestData requestData) {
            this.requestData = requestData;
        }
        
        public RequestData getRequestData() {
        	return requestData;
        }

    }
    
    static class TestObject2 {
        final RequestData requestData;
        final ITestObject1 testObject1;

        @Inject
        TestObject2(final RequestData requestData, final ITestObject1 testObject1) {
            this.requestData = requestData;
            this.testObject1 = testObject1;
        }

    }    

    static interface IData {

    }

    static interface IDataPlus {

    }

    static class Data implements IData, IDataPlus {

    }

    static class DataProvider implements Provider<Data> {

        private final ThreadLocal<Data> slideScanContext = new ThreadLocal<Data>();

        @Override
        public Data get() {
            // New is applied here, so no injection for RequestData
            return getData();
        }

        private Data getData() {
            if (slideScanContext.get() == null) {
                slideScanContext.set(new Data());
            }
            return slideScanContext.get();
        }

    }

    @Test
    public void requestScope() throws Exception {
        Injector injector = Guice.createInjector(new BindingModule());
        TestObject1 testObject1 = injector.getInstance(TestObject1.class);
        TestObject1 testObject2 = injector.getInstance(TestObject1.class);

        // Check that provider can be used to get 2 objects.
        assertNotEquals(testObject1.requestData, testObject2.requestData);
    }
    
    @Test
    public void checkThatProviderIsCalledForEachInstance() throws Exception {
        Injector injector = Guice.createInjector(new BindingModule());
        TestObject2 testObject = injector.getInstance(TestObject2.class);

        // Check that provider can be used to get 2 objects.
        assertNotEquals(testObject.requestData.seqNr, testObject.testObject1.getRequestData().seqNr);
    }    

}
