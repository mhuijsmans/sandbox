package org.mahu.proto.jerseyrest;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.core.MediaType;

import org.junit.Test;
import org.mahu.proto.commons.Chrono;
import org.mahu.proto.jerseyjunittools.RestResource;
import org.mahu.proto.jerseyjunittools.RestResourceUri;
import org.mahu.proto.jerseyjunittools.annotation.RestResourceInTest;

import com.google.common.io.ByteStreams;

public class BigImageTest extends TestBaseClass {

	@Test
	@RestResourceInTest(resource = HelloWorldResource.class)
	public void testMethodStartImage() throws IOException {
		final String[] imageNames = new String[] { "start.png", "weird.png",
				"big-weird.png", "big-weird-compressionLevel9.png" };
		for (String imageName : imageNames) {
			retrieveImageLoop(imageName);
		}
	}
	
	@Test
	@RestResourceInTest(resource = HelloWorld2Resource.class)
	public void testReceiveImageWithCopyToByteBuffer() throws IOException {
		final String[] imageNames = new String[] { "start.png" };
		for (String imageName : imageNames) {
			retrieveImageLoopByteBufferUsingNIO(imageName, false);
		}
	}	

	protected void retrieveImageLoop(final String imageName) throws IOException {
		RestResource<InputStream> resource = new RestResource<InputStream>(
				new RestResourceUri(restService.getBaseURI(),
						"helloworld/images/" + imageName), InputStream.class);
		resource.setMediaType(MediaType.MEDIA_TYPE_WILDCARD);
		//
		Chrono chrono = new Chrono();
		final int MAX = 10;
		long imageSize = -1;
		for (int i = 0; i < MAX; i++) {
			resource.doGet();
			assertTrue("Response=" + resource.getResponseCode(),
					resource.getResponseCode() == 200);
			final int length = resource.getLength();
			System.out.println("Length: " + length);
			InputStream is = resource.getData();
			final byte[] image = ByteStreams.toByteArray(is);
			imageSize = image.length;
		}
		printProgressReport(imageName, chrono, MAX, imageSize);
	}

}
