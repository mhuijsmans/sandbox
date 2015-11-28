package org.mahu.proto.rmitest;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Logger;

public class RmiRegistryWrapper {

	private static final Logger log = Logger.getLogger(RmiRegistryWrapper.class
			.getName());

	@SuppressWarnings("unused")
	private Registry registry;

	public RmiRegistryWrapper() {
		super();
	}

	/**
	 * Start the RmiRgistry service in this process.
	 * It runs in an own thread.
	 */
	public void startRmiRegistery() {
		setSecurityManager();
		try {
			registry = createRegistry();
			log.info("RmiRegistry started");
		} catch (Exception e) {
			log.warning("Exception when starting the registry: "
					+ e.getMessage());
		}
	}

	/**
	 * Stop the RmiRgistry service (that is not possible; throws exception).
	 */
	public void stopRmiRegistery() {
		throw new RuntimeException("Stopping is not possible to my knowledge");
	}

	private void setSecurityManager() {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
	}

	private Registry createRegistry() throws RemoteException {
		Registry registry = LocateRegistry.createRegistry(1099);
		return registry;
	}

}
