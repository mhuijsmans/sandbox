package org.mahu.proto.mavenrpm;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jetty.server.Server;

/**
 * This tutorial was used to build the JettyApp and HelloHandler.
 * http://wiki.eclipse.org/Jetty/Tutorial/Embedding_Jetty 
 */
public class JettyApp {

	public static void main(String[] args) {
    	//
    	System.setProperty("user.dir", args[0]);
    	//
		try {
	        Server server = new Server(8888);
	        server.setHandler(new HelloHandler());
	        
	        server.start();
	        server.join();

		} catch (Exception ex) {
			Logger.getLogger(JettyApp.class.getName()).log(Level.SEVERE, null, ex);
		}

	}
}