package org.mahu.proto.jerseyrest;

import java.util.HashMap;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

public final class ClientFactory {

    private final static Object lock = new Object();
    private final static Client client = createClient();
    private final static HashMap<String, WebTarget> targets = new HashMap<>();

    /**
     * Example: WebTarget target = create("http://localhost:9998", "resource");
     */
    public static WebTarget createWebTarget(final String target, final String resource) {
        WebTarget webTarget = null;
        synchronized (lock) {
            webTarget = targets.get(target);
            if (webTarget == null) {
                webTarget = client.target(target);
                targets.put(target, webTarget);
            }
        }
        // path(..) creates a new WebTarget instance.
        return webTarget.path(resource);
    }

    public static WebTarget createWebTarget(final String target, final String resource, final String queryParam,
            final String queryParamValue) {
        // queryParam(..,..) create a new WebTarget instance.
        return createWebTarget(target, resource).queryParam(queryParam, queryParamValue);
    }

    protected static Client createClient() {
        // source: https://blogs.oracle.com/japod/entry/how_to_use_jersey_client
        // Recommendation: re-use client, re-use webtarget
        // Source has 3 other recommendations.
        Client client = ClientBuilder.newClient();
        //
        // default timeout value for all requests created from this client
        // client.property(ClientProperties.CONNECT_TIMEOUT, 1000);
        // client.property(ClientProperties.READ_TIMEOUT, 1000);

        // It is also possible to overwrite timeout value for a request
        // request.property(ClientProperties.CONNECT_TIMEOUT, 500);
        // request.property(ClientProperties.READ_TIMEOUT, 500);
        return client;
    }

    private ClientFactory() {
        // empty
    }

}
