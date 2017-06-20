package org.mahu.proto.jerseyrest.workflowtest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class WorkFlowTestTest extends WorkFlowTest {

    private RequestProcessingRules rules = new RequestProcessingRules();

    @Before
    public void beforeTest() {
        rules = new RequestProcessingRules();
    }

    @After
    public void afterTest() {
        rules.verifyThatCorrectRequestsAreReceived();
        rules = null;
    }

    @Test
    public void get() {
        final String expectedResponseBody = "bla";

        rules.whenGet("/hello").thenReturnOkWithBody(expectedResponseBody);
        setRules(rules);

        Response response = createRequest().get();
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertEquals(expectedResponseBody, response.readEntity(String.class));
    }

    @Test
    public void delete() {
        rules.returnDefaultResponseFor("/hello");
        setRules(rules);

        Response response = createRequest().delete();

        assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());
    }

    @Test
    public void post_responseBody() {
        final String expectedResponseBody = "bla";

        rules.whenPost("/hello").thenReturnOkWithBody(expectedResponseBody);
        setRules(rules);

        Response response = createRequest().post(null);

        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    @Test
    public void post_resourceCreated() {
        rules.whenPost("/hello").thenReturnResourceCreated();
        setRules(rules);

        Response response = createRequest().post(null);

        assertEquals(HttpServletResponse.SC_CREATED, response.getStatus());
    }

    private Builder createRequest() {
        final String path = "hello";
        final String target = getTarget();
        WebTarget webTarget = ClientFactory.createClient().target(target).path(path);
        return webTarget.request();
    }

    @Test
    public void put() {
        rules.whenPut("/hello").thenReturnNoContentResponse();
        setRules(rules);

        Response response = createRequest().put(Entity.entity("something", MediaType.TEXT_PLAIN_TYPE));

        assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());
    }

    @Test
    public void getNoRuleDefined_statusIsNotImplemented() {
        setRules(rules);

        Response response = createRequest().put(Entity.entity("something", MediaType.TEXT_PLAIN_TYPE));

        assertEquals(HttpServletResponse.SC_NOT_IMPLEMENTED, response.getStatus());
    }

    @Test
    public void delete_alwaysReturn() {
        rules.returnDefaultResponseFor("/hello").always();
        setRules(rules);

        for (int i = 0; i < 5; i++) {
            Response response = createRequest().delete();
            assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());
        }
    }

    @Test
    public void delete_twoTimes() {
        rules.returnDefaultResponseFor("/hello").times(2);
        setRules(rules);

        for (int i = 0; i < 2; i++) {
            Response response = createRequest().delete();
            assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());
        }

        Response response = createRequest().delete();
        assertEquals(HttpServletResponse.SC_NOT_IMPLEMENTED, response.getStatus());
    }

    private String getTarget() {
        return "http://127.0.0.1:" + getPort();
    }

}