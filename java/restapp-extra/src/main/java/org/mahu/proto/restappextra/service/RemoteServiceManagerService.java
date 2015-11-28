package org.mahu.proto.restappextra.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mahu.proto.forkprocesstest.ProcessManager;
import org.mahu.proto.restappext.event.StartSessionCompletedEvent;
import org.mahu.proto.restappext.event.StartupEvent;

import com.google.common.eventbus.Subscribe;

public class RemoteServiceManagerService {

	protected final static Logger LOGGER =LogManager.getLogger(RemoteServiceManagerService.class.getName());

	/**
	 * When set to yes, the output from child processes is immediately printed
	 * to screen.
	 */
	private final boolean printChildDataToConsole = true;

	private final ProcessManager processManager;
	private final Class<?>[] mainClassRemoteService;

	public RemoteServiceManagerService(final Class<?>[] mainClassRemoteService) {
		this.mainClassRemoteService = mainClassRemoteService;
		this.processManager = mainClassRemoteService != null ? new ProcessManager(
				mainClassRemoteService.length) : null;
	}

	@Subscribe
	public void handle(final StartupEvent event) {
		LOGGER.info("ENTER Starting processes");
		StartAllSessionDependantProcesses();
		LOGGER.info("ENTER processes started");
	}

	@Subscribe
	public void handle(final StartSessionCompletedEvent event) {
		LOGGER.info("ENTER restarting processes");
		processManager.StopProcesses();
		StartAllSessionDependantProcesses();
		LOGGER.info("ENTER processes restarted");
	}

	/**
	 * TODO: This may be a temporary solution
	 */
	public void StopProcesses() {
		processManager.StopProcesses();
	}

	private void StartAllSessionDependantProcesses() {
		if (mainClassRemoteService != null) {
			for (Class<?> cls : mainClassRemoteService) {
				processManager.StartProcess(cls, printChildDataToConsole);
			}
		}
	}
}
