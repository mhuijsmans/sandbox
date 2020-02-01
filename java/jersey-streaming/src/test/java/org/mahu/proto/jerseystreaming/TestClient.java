package org.mahu.proto.jerseystreaming;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class TestClient {

	private final int BUFSIZE = 8192 * 8;

	private WebTarget target;

	TestClient(final int port, final String path) {
		Client client = ClientBuilder.newClient();
		target = client.target("http://127.0.0.1:" + port + "/" + path);
	}

	public void getData() throws IOException {
		Response response = target.request(MediaType.APPLICATION_OCTET_STREAM).get();
		try {
			final int contentLength = Integer.parseInt(response.getHeaderString("content-length"));
//		Log.log("TestClient: content-length="+ response.getHeaderString("content-length"));
//		Log.log("TestClient: content-length="+ response.getHeaderString("content-type"));
			int total = 0;
			try (InputStream is = response.readEntity(InputStream.class)) {
				byte[] buf = new byte[BUFSIZE];
				int len;
				while ((len = is.read(buf)) != -1) {
					total += len;
					// Log.log("TestClient: data read="+len+" total="+total);
				}
			}
			if (contentLength != total) {
				throw new RuntimeException("Mismatch content-length=" + contentLength + " and bodylength=" + total);
			}
		} finally {
			response.close();
		}
	}

}
