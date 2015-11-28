package org.mahu.proto.maven.mojo;

import java.util.logging.Logger;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

// copied (modified) from: http://www.hascode.com/2013/07/embedding-jetty-or-tomcat-in-your-java-application/
public class JettyEmbeddedRunner {
	private static final Logger log = Logger
			.getLogger(JettyEmbeddedRunner.class.getName());	
	
	private Server jetty;
	private ServerConnector c;
	
	private final static String ROOT_CONTEXT = "app";
	private final static String IMAGE_CONTEXT = "image";
	
	public int getPort() {
		return 8080;
	}
	
	public String getHost() {
		return "localhost";
	}
	
	public String getPath() {
		return ROOT_CONTEXT+"/"+IMAGE_CONTEXT;
	}	
	
	public void startServer() {
		try {
			jetty = new Server();
			//
			// At shutdown stop the server
			jetty.setStopAtShutdown(true);
			//
			c = new ServerConnector(jetty);
			c.setIdleTimeout(1000);
			c.setAcceptQueueSize(10);
			c.setPort(getPort());
			c.setHost(getHost());
			//
			ServletContextHandler handler = new ServletContextHandler(jetty,
					"/"+ROOT_CONTEXT, true, false);
			ServletHolder servletHolder = new ServletHolder(
					ImageDownloadServlet.class);
			handler.addServlet(servletHolder, "/"+IMAGE_CONTEXT+"/*");
			//
			jetty.addConnector(c);
			jetty.start();
			log.info("Jetty listening on " +getHost()+":"+getPort());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void await() throws InterruptedException {
		// Have current thread wait till server is done running
		jetty.join();
	}
	
	public void stop() throws Exception {
		jetty.stop();
	}
}