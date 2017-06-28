package org.mahu.proto.webguice.rest;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.mahu.proto.webguice.request.GetRequestBindingModule;
import org.mahu.proto.webguice.request.PostRequestBindingModule;
import org.mahu.proto.webguice.request.PostRequestData;
import org.mahu.proto.webguice.stm.IIRequestProcessor;
import org.mahu.proto.webguice.stm.IStateMachine;
import org.mahu.proto.webguice.stm.RequestType;

import com.google.inject.Injector;

@Path("/test")
public class RestService {

    private final IIRequestProcessor requestProcessor;
    private final IStateMachine stateMachine;

    public RestService(final @Context ServletContext servletContext) {
        this(((Injector) servletContext.getAttribute(Injector.class.getName())));
    }

    public RestService(final Injector injector) {
        requestProcessor = injector.getInstance(IIRequestProcessor.class);
        stateMachine = injector.getInstance(IStateMachine.class);
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response getMsg() {
        // Request with no request data
        return requestProcessor.execute(RequestType.GET, new GetRequestBindingModule());
    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public Response postMsg(final String text) {
        // Request with request data
        PostRequestData requestData = new PostRequestData(text);
        return requestProcessor.execute(RequestType.SCAN, new PostRequestBindingModule(requestData));
    }
}
