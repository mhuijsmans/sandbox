package org.mahu.proto.jerseystreaming;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

/**
 * IStreamingProxyGetClient sends a HTTP GET request to the provided target. The
 * Accept-header in the GET request is copied from the HttpHeaders. When the
 * response is received from the target, the status code, content-length/-type
 * (if present) are copied to the returned response. The body is from the target
 * is "streamed", i.e. the JAX-RS STreamingOutput capability is used.
 */
public interface IStreamingProxyGetClient {

	Response proxy(String targetPath, HttpHeaders httpHeaders);
}
