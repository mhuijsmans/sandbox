package org.mahu.proto.forkprocesstest;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.logging.Logger;

/**
 * Process Manager keeps track of started processes and logs status changes.
 */
public class ProcessManager {

	final static Logger logger = Logger.getLogger(ProcessManager.class
			.getName());

	class ChildData {
		private final ChildProcess childProcess;
		private final FutureTask<Integer> future;

		ChildData(final ChildProcess childProcess,
				final FutureTask<Integer> future,
				final boolean printChildDataToConsole) {
			this.childProcess = childProcess;
			this.future = future;
			if (printChildDataToConsole) {
				this.childProcess.printChildDataToConsole();
			}
		}

		String GetName() {
			return childProcess.GetName();
		}

		boolean IsProcessesRunning() {
			return childProcess.IsProcessesRunning();
		}

		int GetExitValue() {
			return childProcess.getExitValue();
		}
	}

	private final ExecutorService executor;
	private final Map<Class<?>, ChildData> childProcesses = new HashMap<>();
	private final List<FutureTask<Integer>> childProcessesTasks = new LinkedList<>();
	private final MonitorService monitorService = new MonitorService();

	public ProcessManager(int nrOfChildprocesses) {
		this.executor = Executors.newFixedThreadPool(nrOfChildprocesses + 1);
	}

	/**
	 * Start a process by closing own JVM parameters and starting the main of
	 * the provided class
	 * 
	 * @param cls
	 *            who main method needs to be called.
	 * @return
	 */
	public FutureTask<Integer> StartProcess(final Class<?> cls) {
		return StartProcess(cls, false);
	}

	/**
	 * Start a process by closing own JVM parameters and starting the main of
	 * the provided class
	 * 
	 * @param cls
	 *            who main method needs to be called.
	 * @param printChildDataToConsole
	 *            when set to yes, will result in printing childdata to console
	 * @return
	 */
	public FutureTask<Integer> StartProcess(final Class<?> cls,
			final boolean printChildDataToConsole) {
		logger.info("StartProcess() ENTER, mainClass=" + cls.getName());
		if (childProcessesTasks.size() == 0) {
			StartProcessStatusLogger();
		}
		if (!childProcesses.containsKey(cls)) {
			ChildProcess fork = new ChildProcess();
			FutureTask<Integer> futureTask = fork
					.createTaskCloneOwnProcess(cls);
			childProcesses.put(cls, new ChildData(fork, futureTask,
					printChildDataToConsole));
			AddToList(futureTask);
			executor.execute(futureTask);
			logger.info("StartProcess() LEAVE");
		}
		return childProcesses.get(cls).future;
	}

	public void StopProcesses() {
		monitorService.StopMonitoring();
		for (ChildData p : childProcesses.values()) {
			p.childProcess.destroyChild();
		}
	}

	public int GetNrOfRunningChildprocesses() {
		return (monitorService != null) ? monitorService
				.GetNrOfRunningChildprocesses() : 0;
	}

	private synchronized void StartProcessStatusLogger() {
		FutureTask<Integer> monitorTask = new FutureTask<Integer>(
				monitorService);
		childProcessesTasks.add(monitorTask);
		executor.execute(monitorTask);
	}

	private synchronized void AddToList(FutureTask<Integer> task) {
		childProcessesTasks.add(task);
	}

	class MonitorService implements Callable<Integer> {
		private boolean isActive;

		public MonitorService() {
			isActive = true;
		}

		@Override
		public Integer call() throws Exception {
			logger.info("MonitorService.class ENTER");
			String status = "";
			while (ShallMonitoring()) {
				StringBuilder sb = new StringBuilder();
				int i = 0;
				synchronized (this) {
					for (ChildData p : childProcesses.values()) {
						if (i++ > 0) {
							sb.append("\n");
						}
						sb.append("..name=")
								.append(p.GetName())
								.append(" state=")
								.append(p.IsProcessesRunning() ? "running"
										: "terminated");
						if (!p.IsProcessesRunning()) {
							sb.append(" exitValue=").append(p.GetExitValue());
						}
					}
				}
				String newStatus = sb.toString();
				if (!status.equals(newStatus)) {
					logger.info("ProcessManager info\n" + newStatus);
					status = newStatus;
				}
				Thread.sleep(1000);
			}
			logger.info("MonitorService.class LEAVE");
			return new Integer(0);
		}

		public synchronized int GetNrOfRunningChildprocesses() {
			int nrOfRunningChildprocesses = 0;
			synchronized (this) {
				for (ChildData p : childProcesses.values()) {
					if (p.IsProcessesRunning()) {
						nrOfRunningChildprocesses++;
					}
				}
			}
			return nrOfRunningChildprocesses;
		}

		public synchronized void StopMonitoring() {
			isActive = false;
		}

		private synchronized boolean ShallMonitoring() {
			return isActive;
		}
	}

}
