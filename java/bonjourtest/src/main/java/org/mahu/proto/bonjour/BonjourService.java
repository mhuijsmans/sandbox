package org.mahu.proto.bonjour;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;
import javax.jmdns.ServiceTypeListener;

/**
 * Wrapper for the BonjourService. This implementation is not thread safe.
 * GIT JmDNS examples:
 * https://github.com/jenkinsci/jmdns/tree/incoming/src/sample/java/samples
 */
public class BonjourService {

	private final static Logger LOGGER = Logger.getLogger(BonjourService.class
			.getName());
	private boolean log = false;
	private JmDNS mDnsService;
	private final String serviceType;

	public BonjourService() {
		this("_http._tcp.local.");
	}

	public BonjourService(final String aServiceType) {
		serviceType = aServiceType;
	}

	public JmDNS getMDnsServer() {
		return mDnsService;
	}

	public String getServiceType() {
		return serviceType;
	}

	public ServiceInfo registerService(final String serviceName,
			final int port, final HashMap<String, String> values) {
		checkNotNull(serviceName);
		checkArgument(port > 0 && port < 65535);
		checkNotNull(values);
		if (createJmDns()) {
			// Register a service.
			// type - fully qualified service type name, such as
			// _http._tcp.local..
			// name - unqualified service instance name, such as foobar
			// port - the local port on which the service runs
			// text - string describing the service
			ServiceInfo service = ServiceInfo.create(serviceType, serviceName, port,
					0, 0, values);
			registerService(service);
			LOGGER.info("Registered service: "+service);
			return service;
		}
		return null;
	}

	public void unregisterService(final ServiceInfo service) {
		if (service != null) {
			mDnsService.unregisterService(service);
		}
	}

	public void register(final ServiceListener listener) {
		checkNotNull(listener);
		if (createJmDns()) {
			mDnsService.addServiceListener(serviceType, listener);
		}
	}

	public void remove(final ServiceListener listener) {
		checkNotNull(listener);
		if (createJmDns()) {
			mDnsService.removeServiceListener(serviceType, listener);
		}
	}

	public void close() {
		if (createJmDns()) {
			try {
				mDnsService.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				LOGGER.severe("IOException");
			} finally {
				mDnsService = null;
			}
		}
	}

	private boolean createJmDns() {
		if (mDnsService == null) {
			/* Activate these lines to see log messages of JmDNS */
			startLogging();
			try {
				InetAddress inetAddress = InetAddress.getByName("127.0.0.1");
				mDnsService = JmDNS.create(inetAddress, "localhost");
				//mDnsService = JmDNS.create();
				mDnsService.addServiceTypeListener(new MyServiceTypeListener());
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				LOGGER.severe("UnknownHostException");
				return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				LOGGER.severe("IOException");
				return false;
			}
		}
		return true;
	}

	private void startLogging() {
		if (log) {
			Logger logger = Logger.getLogger(JmDNS.class.getName());
			ConsoleHandler handler = new ConsoleHandler();
			logger.addHandler(handler);
			logger.setLevel(Level.FINER);
			handler.setLevel(Level.FINER);
		}
	}

	private boolean registerService(final ServiceInfo service) {
		try {
			mDnsService.registerService(service);
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.severe("IOException");
			return false;
		}
	}
	
	   static class MyServiceTypeListener implements ServiceTypeListener {

	        public void serviceTypeAdded(ServiceEvent event) {
	        	LOGGER.fine("Service type added: " + event.getType());
	        }

	        public void subTypeForServiceTypeAdded(ServiceEvent event) {
	        	LOGGER.fine("SubType for service type added: " + event.getType());
	        }
	    }	

}
