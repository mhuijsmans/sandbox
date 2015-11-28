package org.mahu.proto.resttomcat;

import java.net.MalformedURLException;

import javax.servlet.ServletException;

import org.apache.catalina.LifecycleException;
import org.junit.Test;

public class RestTomcatTest {

	@Test
	public void test() throws MalformedURLException, ServletException, LifecycleException {
		Main main = new Main();
		main.start();
		
		// After starting, this URL can be used to access the resoirce:/ / http://127.0.0.1:8080/target/rest/hello
		// - target is the basepath (see main.java)
		// - rest is set as path to map to Jersey (see main.java)
		// - hello is path in rest resource (see HelloResource.java)
	}

}
