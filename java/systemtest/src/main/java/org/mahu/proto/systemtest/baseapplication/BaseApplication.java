package org.mahu.proto.systemtest.baseapplication;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.ws.rs.core.UriBuilder;

import org.mahu.proto.forkprocesstest.ChildProcess;
import org.mahu.proto.systemtest.logging.Logging;
import org.mahu.proto.systemtest.persub.PresenceNotifier;
import org.mahu.proto.systemtest.rest.RestServer;

public class BaseApplication {
	static {
		// This must be the first statement, so logging is in place before it is
		// used.
		Logging.setLoggingProperties();
	}

	protected static Logger log = null;

	protected final List<MonitoredChild> children = new LinkedList<MonitoredChild>();
	protected final ExecutorService executor = Executors.newFixedThreadPool(5);
	protected final String name;
	protected RestServer restServer = null;

	public BaseApplication(final String name) {
		this.name = name;
	}

	protected void startRestService(final int port,
			final Class<?> componentClass) {
		startRestService(port, new Class<?>[] { componentClass }, null);
	}

	protected void startRestService(final int port,
			final Class<?>[] componentClasses, final Object provider) {
		URI uri = UriBuilder.fromUri("http://localhost/").port(port).build();
		restServer = new RestServer(uri);
		for (Class<?> cls : componentClasses) {
			restServer.getResourceConfig().register(cls);
		}
		if (provider != null) {
			restServer.getResourceConfig().register(provider);
		}
		restServer.startServer();
	}

	protected void startApplication(Class<?> appClass) {
		log.info("ChildProcess to be started: " + appClass.getSimpleName());
		ChildProcess child = new ChildProcess();
		child.printChildDataToConsole();
		FutureTask<Integer> futureTask = child
				.createTaskCloneOwnProcess(appClass);
		children.add(new MonitoredChild(child, futureTask));
		executor.execute(futureTask);
	}

	protected void sleepOneSec() throws InterruptedException {
		TimeUnit.SECONDS.sleep(1);
	}

	protected void readSystemInUntilClosure() {
		Scanner scanInput = new Scanner(System.in);
		try {
			String data = scanInput.nextLine();
			log.info("data read: " + data);
		} catch (java.util.NoSuchElementException e) {
			// Exception is throw when System.in closes
			log.info("System.in closes; exception: " + e);
		} finally {
			scanInput.close();
		}
	}

	protected void startNotifierService() {
		FutureTask<Void> presenceNotifierFuture = new FutureTask<Void>(
				new PresenceNotifier(name));
		executor.execute(presenceNotifierFuture);
	}

}
