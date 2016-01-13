package org.mahu.proto.lifecycle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mahu.proto.lifecycle.example2.IEventBus;
import org.mahu.proto.lifecycle.impl.RequestProxyDispatchService;
import org.mahu.proto.lifecycle.impl.RequestProxyEvent;
import org.mockito.Mockito;

public class RequestProxyDispatchServiceTest {

    IEventBus eventBus;

    @Before
    public void prepare() {
        eventBus = Mockito.mock(IEventBus.class);
    }

    @After
    public void cleanup() {
        eventBus = null;
    }

    @Test
    public void start_seviceHasRegisteredOnEventBus() {
        RequestProxyDispatchService service = new RequestProxyDispatchService(eventBus);
        service.start();

        Mockito.verify(eventBus).register(service);
    }

    @Test
    public void stop_start_seviceHasUnregisteredOnEventBus() {
        RequestProxyDispatchService service = new RequestProxyDispatchService(eventBus);
        service.start();

        service.stop();
        Mockito.verify(eventBus).unregister(service);
    }

    @Test
    public void abort_start_seviceHasUnregisteredOnEventBus() {
        RequestProxyDispatchService service = new RequestProxyDispatchService(eventBus);
        service.start();

        service.abort();
        Mockito.verify(eventBus).unregister(service);
    }

    @Test
    public void process_afterStart_seviceHasUnregisteredOnEventBus() {
        RequestProxyDispatchService service = new RequestProxyDispatchService(eventBus);
        service.start();
        RequestProxyEvent requestProxyEvent = Mockito.mock(RequestProxyEvent.class);

        service.process(requestProxyEvent);
        Mockito.verify(requestProxyEvent).execute();
    }

    @Test
    public void process_afterStop_seviceHasUnregisteredOnEventBus() {
        RequestProxyDispatchService service = new RequestProxyDispatchService(eventBus);
        service.start();
        service.stop();
        RequestProxyEvent requestProxyEvent = Mockito.mock(RequestProxyEvent.class);

        try {
            service.process(requestProxyEvent);
            fail("Calling execute after stop shall throw exception");
        } catch (RuntimeException e) {
            assertEquals(RequestProxyDispatchService.SERVICE_UNAVAILABLE, e.getMessage());
        }
    }
    
    @Test
    public void process_afterAbort_seviceHasUnregisteredOnEventBus() {
        RequestProxyDispatchService service = new RequestProxyDispatchService(eventBus);
        service.start();
        service.abort();
        RequestProxyEvent requestProxyEvent = Mockito.mock(RequestProxyEvent.class);

        try {
            service.process(requestProxyEvent);
            fail("Calling execute after abort shall throw exception");
        } catch (RuntimeException e) {
            assertEquals(RequestProxyDispatchService.SERVICE_UNAVAILABLE, e.getMessage());
        }
    }    

}
