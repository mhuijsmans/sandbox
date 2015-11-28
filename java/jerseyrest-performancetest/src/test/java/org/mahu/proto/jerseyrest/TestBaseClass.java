package org.mahu.proto.jerseyrest;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.ws.rs.core.MediaType;

import org.junit.Rule;
import org.mahu.proto.commons.Chrono;
import org.mahu.proto.jerseyjunittools.RestResource;
import org.mahu.proto.jerseyjunittools.RestResourceUri;
import org.mahu.proto.jerseyjunittools.RestServiceRule;

import com.google.common.io.ByteStreams;

public class TestBaseClass {

	protected static final byte[] buf = new byte[8092 * 8];
	
	@Rule
	public RestServiceRule restService = new RestServiceRule();
	
	static TestBaseClass me;
	TestBaseClass() {
		me = this;
	}
	
	protected static void printProgressReportWithMsg(final String msg , Chrono chrono,
			final int MAX, long imageSize) {
		System.out.println(msg + ", size=" + imageSize + "; "
				+ chrono.elapsedAndAvg(MAX) + " kb/sec: "
				+ (imageSize * 1000 * MAX) / (chrono.elapsedMs() * 1024));
	}	

	protected static void printProgressReport(Chrono chrono,
			final int MAX, long imageSize) {
		System.out.println("Size=" + imageSize + "; "
				+ chrono.elapsedAndAvg(MAX) + " kb/sec: "
				+ (imageSize * 1000 * MAX) / (chrono.elapsedMs() * 1024));
	}
	
	protected static void printProgressReport(final String imageName, Chrono chrono,
			final int MAX, long imageSize) {
		System.out.println("Image=" + imageName + "; size=" + imageSize + "; "
				+ chrono.elapsedAndAvg(MAX) + " kb/sec: "
				+ (imageSize * 1000 * MAX) / (chrono.elapsedMs() * 1024));
	}	

	protected static void retrieveImageLoopByteBufferUsingNIO(final String imageName,
			boolean copyToNio) throws IOException {
		RestResource<InputStream> resource = new RestResource<InputStream>(
				new RestResourceUri(me.restService.getBaseURI(),
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
			// System.out.println("Length: " + length);
			InputStream is = resource.getData();
			imageSize = readFromInputStreamUsingNIO(copyToNio, buf, length, is);
		}
		printProgressReport(imageName, chrono, MAX, imageSize);
	}

	protected static long readFromInputStreamUsingNIO(boolean copyToNioOrWrapByteArray, final byte[] buf,
			final long length, InputStream is) throws IOException,
			AssertionError {
		long imageSize;
		if (copyToNioOrWrapByteArray) {
			final byte[] image = ByteStreams.toByteArray(is);
			imageSize = image.length;
			ByteBuffer nioBuffer = ByteBuffer.wrap(image);
			// System.out.println("nioBuffer capacity: "
			// + nioBuffer.capacity());
		} else {
			imageSize = length;
			ByteBuffer nioBuffer = ByteBuffer.allocate((int) length);
			nioBuffer.clear();
			// System.out.println("nioBuffer capacity: "
			// + nioBuffer.capacity());
			int bytesRead = 0;
			while (bytesRead < length) {
				int read = is.read(buf);
				if (read > 0) {
					nioBuffer.put(buf, 0, read);
					bytesRead += read;
				} else {
					throw new AssertionError("Not expecting this");
				}
			}
		}
		is.close();
		return imageSize;
	}


}
