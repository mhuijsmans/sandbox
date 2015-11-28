package org.mahu.proto.embeddedjetty;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class RESTServer {

	private Server jettyServer;

	/**
	 * Start Jetty at the specified port
	 * @param port
	 * @param restServicesClassNames use as seperator ;
	 * @throws Exception
	 */
	public void start(final int port, String restServicesClassNames) throws Exception {
		ServletContextHandler context = new ServletContextHandler(
				ServletContextHandler.SESSIONS);
		context.setContextPath("/");

		jettyServer = new Server(port);
		jettyServer.setHandler(context);

		ServletHolder jerseyServlet = context.addServlet(
				org.glassfish.jersey.servlet.ServletContainer.class, "/*");
		jerseyServlet.setInitOrder(0);

		// Tells the Jersey Servlet which REST service/class to load.
		jerseyServlet.setInitParameter(
				"jersey.config.server.provider.classnames",
				restServicesClassNames);
		jettyServer.start();
	}
	
	public void join() throws InterruptedException {
		if (jettyServer != null) {
			jettyServer.join();
		}
	}	

	public void stop() throws Exception {
		if (jettyServer != null) {
			jettyServer.stop();
			jettyServer.destroy();
			jettyServer = null;
		}
	}
}
