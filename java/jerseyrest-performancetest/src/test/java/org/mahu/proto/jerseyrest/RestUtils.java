package org.mahu.proto.jerseyrest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import com.google.common.io.ByteStreams;

public class RestUtils {

	public static Response writeResourceToResponse(final int nrOfBytes) {
		final byte[] b = new byte[nrOfBytes];
		Response resp = null;
		resp = Response.ok().entity(new StreamingOutput() {
			public void write(OutputStream output) throws IOException,
					WebApplicationException {
				output.write(b);
				output.flush();
			}
		}).header( "Content-Length", nrOfBytes).build();
		return resp;
	}

	public static Response writeResourceToResponse(final String resourcePath) {
		URL url = RestUtils.class.getClassLoader().getResource(resourcePath);
		if (url == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		InputStream is = null;
		Response resp = null;
		try {
			is = url.openStream();
			resp = copyStreamToResponse(is);
		} catch (IOException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		} finally {
			closeStream(is);
		}
		return resp;
	}

	private static Response copyStreamToResponse(final InputStream is)
			throws IOException {
		final byte[] image = ByteStreams.toByteArray(is);
		final byte[] finalImage = image;
		Response resp = Response.ok().entity(new StreamingOutput() {
			public void write(OutputStream output) throws IOException,
					WebApplicationException {
				output.write(finalImage);
				output.flush();
			}
		}).header( "Content-Length", image.length).build();
		return resp;
	}

	private static void closeStream(InputStream is) {
		if (is != null) {
			try {
				is.close();
			} catch (IOException e) {
				// todo: investigate what can be cause and if warning is needed
			}
		}
	}

}
