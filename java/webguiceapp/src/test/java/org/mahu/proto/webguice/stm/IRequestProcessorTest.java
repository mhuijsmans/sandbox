package org.mahu.proto.webguice.stm;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.junit.Test;
import org.mahu.proto.webguice.request.RequestCommonBindingsModule;
import org.mahu.proto.webguice.workflow.ITaskListExecutor;
import org.mahu.proto.webguice.workflow.IWorkFlowExecutor;
import org.mahu.proto.webguice.workflow.WorkflowModule;
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
            ;
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

    static class ScopeTestRequest implements IRequest {

        final IWorkFlowExecutor workFlowExecutor1;
        final ITaskListExecutor taskListExecutor1;
        final IWorkFlowExecutor workFlowExecutor2;
        final ITaskListExecutor taskListExecutor2;

        @Inject
        ScopeTestRequest(final IWorkFlowExecutor workFlowExecutor, final ITaskListExecutor taskListExecutor,
                final Injector injector) {
            this.workFlowExecutor1 = workFlowExecutor;
            this.taskListExecutor1 = taskListExecutor;

            workFlowExecutor2 = injector.getInstance(IWorkFlowExecutor.class);
            taskListExecutor2 = injector.getInstance(ITaskListExecutor.class);
        }

        @Override
        public Response execute() {
            return null;
        }

    }

    static class SaveRequestStateMachine implements IStateMachine {

        ScopeTestRequest request;

        @Override
        public Response execute(IRequestProvider requestProvider) {
            request = (ScopeTestRequest) requestProvider.get();
            return null;
        }
    };

    static class RequestScopeTestModule extends AbstractModule {
        @Override
        protected void configure() {
            install(new WorkflowModule());
            install(new RequestCommonBindingsModule());
            bind(IRequest.class).to(ScopeTestRequest.class);
        }
    };

}
