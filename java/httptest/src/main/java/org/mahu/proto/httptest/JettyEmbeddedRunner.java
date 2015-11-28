package org.mahu.proto.httptest;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

// copied (modified) from: http://www.hascode.com/2013/07/embedding-jetty-or-tomcat-in-your-java-application/
public class JettyEmbeddedRunner {
	private Server jetty;
	private ServerConnector c;
	
	public int getPort() {
		return 8080;
	}
	
	public String getDatePath() {
		return "app/date";
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
			c.setPort(8080);
			c.setHost("localhost");
			//
			ServletContextHandler handler = new ServletContextHandler(jetty,
					"/app", true, false);
			ServletHolder servletHolder = new ServletHolder(
					DatePrintServlet.class);
			handler.addServlet(servletHolder, "/date");
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