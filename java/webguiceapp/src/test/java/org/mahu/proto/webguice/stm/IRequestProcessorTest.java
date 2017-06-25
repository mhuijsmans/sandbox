package org.mahu.proto.webguice.stm;

import org.junit.Test;
import org.mockito.Mockito;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;

public class IRequestProcessorTest {

    static class TestBindingModule extends AbstractModule {

        private final IRequest request;

        TestBindingModule(final IRequest request) {
            this.request = request;
        }

        @Override
        protected void configure() {
            bind(IRequest.class).toInstance(request);
        }
    }

    @Test
    public void execute_stateMachineCalled() {
        IStateMachine stateMachine = Mockito.mock(IStateMachine.class);
        Injector injector = Mockito.mock(Injector.class);
        IRequest request = Mockito.mock(IRequest.class);

        IIRequestProcessor requestprocessor = new IRequestProcessor(stateMachine, injector);
        requestprocessor.execute(RequestType.GET, new TestBindingModule(request));

        Mockito.verify(stateMachine, Mockito.times(1)).execute(Mockito.any());
    }

}
