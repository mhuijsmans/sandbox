package org.mahu.proto.bonjour;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;

public class App2 {
	private final static Logger LOGGER = Logger.getLogger(App2.class.getName());

	private static BonjourService serv;

	public static void main(String[] args) throws IOException {
		serv = new BonjourService();
		serv.register(mdnsServiceListener);
		//
		final HashMap<String, String> values = new HashMap<String, String>();
		values.put("RemV", "10000");
		serv.registerService("app2", 6667, values);
		//
		System.out.println("Presso any key to exit...");
		System.in.read();
	}

	private static ServiceListener mdnsServiceListener = new ServiceListener() {
		public void serviceAdded(ServiceEvent serviceEvent) {
			LOGGER.info("serviceAdded, event: " + serviceEvent);
			// Test service is discovered. requestServiceInfo() will trigger
			// serviceResolved() callback.
			serv.getMDnsServer().requestServiceInfo(serv.getServiceType(),
					serviceEvent.getName());
		}

		public void serviceRemoved(ServiceEvent serviceEvent) {
			LOGGER.info("serviceRemoved, event: " + serviceEvent);
		}

		public void serviceResolved(ServiceEvent serviceEvent) {
			LOGGER.info("serviceResolved, event: " + serviceEvent);
		}
	};

}
