package org.mahu.proto.embeddedtomcat;

import java.io.File;
import java.net.MalformedURLException;

import javax.servlet.ServletException;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

public class RESTServer {

	Tomcat tomcat = new Tomcat();

	public void start(final int port, Class<?>... clss)
			throws ServletException, LifecycleException, MalformedURLException {

		startNoAwait(port, clss);
		tomcat.getServer().await();
	}

	public void startNoAwait(final int port, Class<?>... clss)
			throws ServletException, LifecycleException, MalformedURLException {

		tomcat.setPort(port);

		// Define a folder that holds web application content.
		String webappDirLocation = "target";
		// Define a web application context.
		Context context = tomcat.addWebapp("/target", new File(
				webappDirLocation).getAbsolutePath());

		// Add servlet that will register Jersey REST resources
		Tomcat.addServlet(context, "jersey-container-servlet",
				resourceConfig(clss));
		context.addServletMapping("/rest/*", "jersey-container-servlet");

		tomcat.start();
	}

	public void stop() {
		try {
			tomcat.stop();
		} catch (LifecycleException e) {
			e.printStackTrace();
		}
	}

	private ServletContainer resourceConfig(Class<?>... clss) {
		return new ServletContainer(new ResourceConfig(clss));
	}

}
