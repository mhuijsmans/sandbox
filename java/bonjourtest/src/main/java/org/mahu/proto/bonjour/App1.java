package org.mahu.proto.bonjour;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

/**
 * Hello world!
 * 
 */
public class App1 {
	private final static Logger LOGGER = Logger.getLogger(App1.class.getName());
	private static BonjourService serv;

	public static void main(String[] args) throws IOException {
		serv = new BonjourService();
		//
		serv.register(mdnsServiceListener);
		//
		final HashMap<String, String> values = new HashMap<String, String>();
		values.put("RemV", "10000");
		serv.registerService("app1", 6666, values);
		//
		System.out.println("Press any key to exit...");
		System.in.read();
	}

	private static ServiceListener mdnsServiceListener = new ServiceListener() {
		public void serviceAdded(ServiceEvent serviceEvent) {
			LOGGER.info("serviceAdded, event: " + serviceEvent);
			// Test service is discovered. requestServiceInfo() will trigger
			// serviceResolved() callback.
//			serv.getMDnsServer().requestServiceInfo(serv.getServiceType(),
//					serviceEvent.getName());
			LOGGER.info("Registered service: "+serviceEvent.getInfo());
		}

		public void serviceRemoved(ServiceEvent serviceEvent) {
			LOGGER.info("serviceRemoved, event: " + serviceEvent);
		}

		public void serviceResolved(ServiceEvent serviceEvent) {
			LOGGER.info("serviceResolved, event: " + serviceEvent);
		}

	};
}
