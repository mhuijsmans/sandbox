package org.mahu.proto.restappextra.remoteservice;

import javax.ws.rs.core.MediaType;

import org.mahu.proto.jerseytools.RestResource;
import org.mahu.proto.jerseytools.RestResourceUri;
import org.mahu.proto.restappextra.TestConst;

public class MeetupClient {

	/**
	 * Join meeting. Retry is needed. It it fails, throw exception.
	 */
	public static void tellImPresent() {
		System.out.println(MeetupClient.class + " tellImPresent");
		RestResourceUri uri = new RestResourceUri("http://localhost:"
				+ TestConst.MEETUP_PORT, "/noknok");
		RestResource<String> resource = new RestResource<String>(uri,
				String.class);
		resource.setMediaType(MediaType.TEXT_PLAIN);
		int attempts = 5;
		while (attempts > 0) {
			resource.doGet();
			System.out.println(MeetupClient.class + " tellImPresent"
					+ " http.response.status=" + resource.getResponseCode());
			attempts--;
			if (resource.getResponseCode() == 200) {
				attempts = 0;
			}
		}
		if (resource.getResponseCode() != 200) {
			throw new RuntimeException("Failed to join meetup");
		}
	}

}
