package org.mahu.proto.jerseystreaming;

import java.io.IOException;
import java.io.OutputStream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.Response.ResponseBuilder;

@Path("abc")
@Produces(MediaType.APPLICATION_OCTET_STREAM)
public class TestResource {

	private static final int SLEEP_IN_MS = 0;

	@GET
	public Response streamData(@Context HttpHeaders httpHeaders) {

		// Log.log("TestResource: Accept=" + httpHeaders.getHeaderString("Accept"));

		final int MAX = 1000;
		final int WRITE_SIZE = 4096*8;

		StreamingOutput stream = new StreamingOutput() {

			@Override
			public void write(OutputStream output) throws IOException, WebApplicationException {
				byte[]b = new byte[WRITE_SIZE];
				for (int i = 0; i < MAX; i++) {
					writeBytes(output, b);
					sleepInMs(SLEEP_IN_MS);
				}
			}
		};
		final ResponseBuilder response = Response.ok(stream);
		response.header("content-length", MAX * WRITE_SIZE);
		return response.build();
	}

	private static void sleepInMs(final int timeInMs) throws IOException {
		if (timeInMs > 0) {
			try {
				Thread.sleep(timeInMs);
			} catch (InterruptedException e) {
				throw new IOException(e);
			}
		}
	}

	private static void writeBytes(OutputStream output, byte[]b) throws IOException {
		output.write(b);
		output.flush();
		// Log.log("TestResource has written " + b.length + " bytes");
	}

}
