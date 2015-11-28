package org.mahu.proto.embeddedtomcat;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

public class HttpClient {
	private static final Logger log = Logger.getLogger(HttpClient.class
			.getName());

	public String response = null;
	public int reponseCode = -1;

	class HttpClientException extends Exception {
		private static final long serialVersionUID = 1L;

		HttpClientException(Exception e) {
			super(e);
		}
	}

	public void doGet(final String urlString) throws HttpClientException {
		clear();
		log.info("urlString: " + urlString);
		URL url = null;
		HttpURLConnection connection = null;
		InputStream stream = null;
		try {
			url = new URL(urlString);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
			// Next line results in actual remote call
			stream = connection.getInputStream();
			reponseCode = connection.getResponseCode();
			if (reponseCode == 200) {
				response = readString(stream);
			}
		} catch (IOException e) {
			throw new HttpClientException(e);
		} finally {
			closeStream(stream);
		}
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

	private void clear() {
		response = null;
		reponseCode = -1;
	}

	private String readString(final InputStream stream) throws IOException {
		Reader reader = new InputStreamReader(stream, "UTF-8");
		StringBuilder textBuilder = new StringBuilder();
		int c = 0;
		while ((c = reader.read()) != -1) {
			textBuilder.append((char) c);
		}
		return textBuilder.toString();
	}
}
