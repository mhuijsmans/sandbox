package org.mahu.proto.jerseyrest.workflowtest;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.Response;

class RequestProcessingRules implements IRequestProcessingRules {

    public final static int OK = HttpServletResponse.SC_OK;
    public final static int NO_CONTENT = HttpServletResponse.SC_NO_CONTENT;
    public final static int NOT_FOUND = HttpServletResponse.SC_NOT_FOUND;
    public final static int CREATED = HttpServletResponse.SC_CREATED;
    public final static int NOT_IMPLEMENTED = HttpServletResponse.SC_NOT_IMPLEMENTED;

    private final Object lock = new Object();
    private final Map<String, RequestProcessingRule> rules = new HashMap<>();

    public RequestProcessingRule whenGet(String path) {
        return when(HttpMethod.GET, path, NOT_FOUND);
    }

    public RequestProcessingRule whenDelete(String path) {
        return when(HttpMethod.DELETE, path, NO_CONTENT);
    }

    public RequestProcessingRule whenPost(String path) {
        return when(HttpMethod.POST, path, NOT_FOUND);
    }

    public RequestProcessingRule whenPut(String path) {
        return when(HttpMethod.PUT, path, NO_CONTENT);
    }

    public RequestProcessingRule returnDefaultResponseFor(String path) {
        return this.whenDelete(path).thenReturnDefaultResponse();
    }

    private RequestProcessingRule when(String method, String path, final int statusCode) {
        final String key = createKey(method, path);
        RequestProcessingRule rule = new RequestProcessingRule(key, statusCode);
        RequestProcessingRule previousRule = rules.put(key, rule);
        if (previousRule != null) {
            throw new RuntimeException("Duplicate rule, method=" + method + ", path=" + path);
        }
        return rule;
    }

    @Override
    public Response getResponse(String method, String path) {
        final String key = createKey(method, path);
        System.out.println("key=" + key);
        synchronized (lock) {
            Optional<Response> response = Optional.empty();
            if (rules.containsKey(key)) {
                response = rules.get(key).getResponse();
            }
            if (response.isPresent()) {
                return response.get();
            } else {
                // If no response is defined, return not implemented
                return Response.status(NOT_IMPLEMENTED).build();
            }
        }
    }

    @Override
    public void verifyThatCorrectRequestsAreReceived() {
        synchronized (lock) {
            for (RequestProcessingRule rule : rules.values()) {
                rule.verifyCorrectNumberOfRequestsReceived();
            }
        }
    }

    private static String createKey(String method, String path) {
        return method + path;
    }

}
