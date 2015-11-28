package org.mahu.proto.resttomcat;

import java.io.File;
import java.net.MalformedURLException;

import javax.servlet.ServletException;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

public class Main {

	public static void main(String[] args) throws Exception, LifecycleException {
		new Main().start();
	}

	public void start() throws ServletException, LifecycleException,
			MalformedURLException {

		Tomcat tomcat = new Tomcat();

		String webPort = System.getenv("PORT");
		if (webPort == null || webPort.isEmpty()) {
			webPort = "8080";
		}
		tomcat.setPort(Integer.valueOf(webPort));

		// Define a folder that holds web application content.
		String webappDirLocation = "target";
		// Define a web application context.		
		Context context = tomcat.addWebapp("/target", new File(
				webappDirLocation).getAbsolutePath());

		// Add servlet that will register Jersey REST resources
		Tomcat.addServlet(context, "jersey-container-servlet", resourceConfig());
		context.addServletMapping("/rest/*", "jersey-container-servlet");

		tomcat.start();
		tomcat.getServer().await();
	}

	private ServletContainer resourceConfig() {
		return new ServletContainer(new ResourceConfig(HelloResource.class,
				Hello1Resource.class));
	}

}