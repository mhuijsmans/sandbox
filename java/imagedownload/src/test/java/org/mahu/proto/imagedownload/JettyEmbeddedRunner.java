package org.mahu.proto.imagedownload;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

// copied (modified) from: http://www.hascode.com/2013/07/embedding-jetty-or-tomcat-in-your-java-application/
public class JettyEmbeddedRunner {
	private static String APP =  "/app";
	private static String PATH =  "/image";
	
	private Server jetty;
	private ServerConnector c;
	
	public String getPath() {
		return APP+PATH;
	}	
	
	public void startServer(final String host, final int port) {
		// ref: http://stackoverflow.com/questions/9235809/how-to-enable-debug-level-logging-with-jetty-embedded
		System.setProperty("org.eclipse.jetty.LEVEL","INFO");
		try {
			jetty = new Server();
			//
			// At shutdown stop the server
			jetty.setStopAtShutdown(true);
			//
			c = new ServerConnector(jetty);
			c.setIdleTimeout(1000);
			c.setAcceptQueueSize(10);
			c.setPort(port);
			c.setHost(host);
			//
			ServletContextHandler handler = new ServletContextHandler(jetty,
					APP, true, false);
			ServletHolder servletHolder = new ServletHolder(
					DownloadImageServlet.class);
			handler.addServlet(servletHolder, PATH+"/*");
			//
			jetty.addConnector(c);
			jetty.start();
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