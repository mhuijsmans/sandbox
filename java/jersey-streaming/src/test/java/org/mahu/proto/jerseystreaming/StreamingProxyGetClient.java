package org.mahu.proto.jerseystreaming;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;

final class StreamingProxyGetClient implements IStreamingProxyGetClient {

	private static final int START_INDEX = 0;

	// What is a good value for MAX_BUFFER_SIZE?
	// 50% of the L1 cache provided best performance in test on taptop.
	private static final int MAX_BUFFER_SIZE = 1024 * 32;

	private static final String ACCEPT = "Accept";
	private static final String CONTENT_TYPE = "Content-Type";
	private static final String CONTENT_LENGTH = "Content-Length";

	StreamingProxyGetClient() {
	}

	@Override
	public Response proxy(final String targetPath, final HttpHeaders httpHeaders) {
		
		// TODO: catch connection error and convert to a 404 not found with reason connectionError. 
		final Response clientResponse = makeGetRequest(targetPath, httpHeaders);
		
		return proxyResponse(clientResponse);
	}
	
	private Response proxyResponse(final Response clientResponse) {

		// Create a response that holds same status code as received from the server. 
		final ResponseBuilder response = Response.status(clientResponse.getStatus());

		// Proxy the body received from the server.
		final int contentLength = determineContentLength(clientResponse);
		if (contentLength > 0) {
			copyHeader(clientResponse, response, CONTENT_LENGTH);
			copyHeader(clientResponse, response, CONTENT_TYPE);
			// The StreamingOutput will close the clientResponse once all data is read. 
			final StreamingOutput stream = createStreamingOutput(clientResponse, contentLength);
			response.entity(stream);
		} else {
			close(clientResponse);
		}
		
		return response.build();
	}

	private static Response makeGetRequest(final String targetPath, final HttpHeaders httpHeaders) {
		final Client client = ClientBuilder.newClient();
		final WebTarget target = client.target(targetPath);
		// Copy Accept header into the target request.
		final String acceptHeaderValue = httpHeaders.getHeaderString(ACCEPT);
		final Response response = target.request().header(ACCEPT, acceptHeaderValue).get();
		return response;
	}

	private StreamingOutput createStreamingOutput(final Response clientResponse, final int contentLength) {
		final StreamingOutput stream = new StreamingOutput() {

			@Override
			public void write(OutputStream outputStream) throws IOException {
				try {
					readDataFromServerAndWriteToOutputStream(clientResponse, outputStream, contentLength);
				} finally {
					close(clientResponse);
				}
			}
		};
		
		return stream;
	}
	
	private void readDataFromServerAndWriteToOutputStream(final Response response, final OutputStream output, final int contentLength)
			throws IOException {
		try (InputStream is = response.readEntity(InputStream.class)) {
			final int bufferSize = contentLength >= MAX_BUFFER_SIZE ? MAX_BUFFER_SIZE : contentLength;
			byte[] buf = new byte[bufferSize];
			int len;
			while ((len = is.read(buf)) != -1) {
				output.write(buf, START_INDEX, len);
				output.flush();
				// Log.log("ProxyClient: data proxied=" + len);
			}
		}
	}	

	private static void close(final Response clientResponse) {
		// response/stream must be closed to release OS resources and prevent memory-leaks.
		clientResponse.close();
	}

	private static int determineContentLength(final Response clientResponse) {
		final String value = clientResponse.getHeaderString(CONTENT_LENGTH);
		if (value == null) {
			return -1;
		}
		return Integer.parseInt(value);
	}

	private static void copyHeader(final Response clientResponse, final ResponseBuilder response, final String header) {
		final String value = clientResponse.getHeaderString(header);
		// Log.log("GetProxyClient: " + header + "=" + value);
		if (value != null) {
			response.header(header, value);
		}
	}

}
