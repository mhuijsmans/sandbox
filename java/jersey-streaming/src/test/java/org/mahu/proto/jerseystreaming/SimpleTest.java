package org.mahu.proto.jerseystreaming;

import java.io.IOException;

import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;

public class SimpleTest extends JerseyTest {

	@Override
	protected Application configure() {
		forceSet(TestProperties.CONTAINER_PORT, "0");
		return new ResourceConfig(GetProxyResource.class, TestResource.class);
	}

	@Test
	public void test() throws IOException {
		// Log.log("SimpleTest: serverport=" + getPort());

		final long start = System.currentTimeMillis();
		final int max = 100;
		for (int i = 0; i < max; i++) {
			TestClient client = new TestClient(getPort(), "proxy");
			client.getData();
		}
		final long end = System.currentTimeMillis();
		Log.log("elapsed=" + ((end - start)/max));
	}

}
