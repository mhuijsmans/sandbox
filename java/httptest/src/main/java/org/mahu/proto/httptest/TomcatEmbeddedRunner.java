package org.mahu.proto.httptest;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.startup.Tomcat;

// copied (modified) from: http://www.hascode.com/2013/07/embedding-jetty-or-tomcat-in-your-java-application/
public class TomcatEmbeddedRunner {
	private Tomcat tomcat;
	
	public int getPort() {
		return 8080;
	}
	
	public String getDatePath() {
		return "app/date";
	}	

	public void startServer() throws LifecycleException {
		tomcat = new Tomcat();
		tomcat.setPort(8080);
		//
		// Tomcat doesn't seem to have a setStopAtShutdown(..) like Jetty. 
		//
		// Tomcat 7 JavaDoc https://tomcat.apache.org/tomcat-7.0-doc/api/org/apache/catalina/startup/Tomcat.html#addContext(java.lang.String, java.lang.String)
		// for public Context addContext(String contextPath, String baseDir)
		// Tomcat has concept of virtual host, to which Context's are added.
		File base = new File(System.getProperty("java.io.tmpdir"));
		Context rootCtx = tomcat.addContext("/app", base.getAbsolutePath());
		//
		Tomcat.addServlet(rootCtx, "dateServlet", new DatePrintServlet());
		rootCtx.addServletMapping("/date", "dateServlet");
		//
		tomcat.start();
	}
	
	public void await() {
		tomcat.getServer().await();
	}

	public void stop() throws LifecycleException, InterruptedException {
		tomcat.stop();
		while (tomcat.getService().getState() != LifecycleState.STOPPED) {
			TimeUnit.MILLISECONDS.sleep(50);
		}
	}
}