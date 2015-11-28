package org.mahu.proto.maven.mojo;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class HttpClient {
	private final org.apache.maven.plugin.logging.Log log;
	public String response = null;
	public int reponseCode = -1;
	private String reponseMessage = null;
	private byte[] bodyBytes = null;

	class HttpClientException extends Exception {
		private static final long serialVersionUID = 1L;

		HttpClientException(Exception e) {
			super(e);
		}
	}

	HttpClient(org.apache.maven.plugin.logging.Log log) {
		this.log = log;
	}

	public void doGet(final String urlString) throws HttpClientException {
		clear();
		log.info("urlString: " + urlString);
		HttpURLConnection connection = null;
		InputStream stream = null;
		try {
			connection = createHttpUrlConnection(urlString);
			// Next line results in actual remote call
			stream = connection.getInputStream();
			reponseCode = connection.getResponseCode();
			reponseMessage = connection.getResponseMessage();
			readBodyHttpResponse(connection, stream);
		} catch (IOException e) {
			closeStream(stream);
			throw new HttpClientException(e);
		}
	}

	protected HttpURLConnection createHttpUrlConnection(final String urlString)
			throws MalformedURLException, IOException, ProtocolException {
		HttpURLConnection connection;
		URL url = new URL(urlString);
		connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.connect();
		return connection;
	}

	protected void readBodyHttpResponse(HttpURLConnection connection,
			InputStream stream) throws IOException {
		final int length = connection.getContentLength();
		if (length <= 0) {
			throw new IOException("ContentLength <=0");
		}
		bodyBytes = new byte[length];
		int offset = 0;
		while (offset < length) {
			int bytesRead = stream.read(bodyBytes, offset, (length - offset));
			if (bytesRead <= 0) {
				throw new IOException("Read returned <=0");
			}
			offset += bytesRead;
		}
	}

	public byte[] getBytes() {
		return bodyBytes;
	}

	protected void closeStream(InputStream stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}

	public int getResponseCode() {
		return reponseCode;
	}
	
	public String getReponseMessage() {
		return reponseMessage;
	}	

	private void clear() {
		response = null;
		reponseCode = -1;
		reponseMessage = null;
		bodyBytes = null;
	}
}
