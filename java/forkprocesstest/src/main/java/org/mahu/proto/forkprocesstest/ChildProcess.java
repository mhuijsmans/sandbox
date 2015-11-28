package org.mahu.proto.forkprocesstest;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class ChildProcess {

	private static final Logger LOGGER = Logger.getLogger(ChildProcess.class
			.getName());

	private String name = "?";
	private String[] jvmOptions = new String[0];
	private OutputStream streamToChild;
	private CollectChildOutputData outputData;
	private CollectChildOutputData errorData;
	private int exitValue;
	private Process process;
	private boolean isRunning = false;
	private boolean printToConsole = false;

	static class ForkProcessException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		ForkProcessException(String msg) {
			super(msg);
		}

		ForkProcessException(String msg, Exception e) {
			super(msg, e);
		}
	}
	
	public String GetName() {
		return name;
	}

	public void setJvmOptions(final String[] jvmOptions) {
		checkNotNull(jvmOptions);
		this.jvmOptions = jvmOptions;
	}

	public void printChildDataToConsole() {
		printToConsole = true;
	}

	class ChildProcessCallable implements Callable<Integer> {

		private final Class<?> mainClass;
		private final String argument;

		public ChildProcessCallable(final Class<?> mainClass, final String argument) {
			this.mainClass = mainClass;
			this.argument = argument;
		}

		@Override
		public Integer call() throws Exception {
			return cloneOwnProcess(mainClass, argument);
		}
	}

	public FutureTask<Integer> createTaskCloneOwnProcess(
			final Class<?> mainClass) {
		name = mainClass.getName();
		return new FutureTask<Integer>(new ChildProcessCallable(mainClass,null));
	}
	
	public FutureTask<Integer> createTaskCloneOwnProcess(
			final Class<?> mainClass,final String argument) {
		return new FutureTask<Integer>(new ChildProcessCallable(mainClass, argument));
	}


	/**
	 * Start a child process using the properties & environment data from this
	 * process.
	 * 
	 * @param mainClass
	 *            , class whose main() method to invoke
	 * @return the exitValue returned by the childProcess
	 */
	public int cloneOwnProcess(final Class<?> mainClass) {
		return cloneOwnProcess(mainClass, null);
	}

	/**
	 * Start a child process using the properties & environment data from this
	 * process.
	 * 
	 * @param mainClass
	 *            , class whose main() method to invoke
	 * @param argument
	 *            argument provided to the main class
	 * @return the exitValue returned by the childProcess
	 */
	public int cloneOwnProcess(final Class<?> mainClass, final String argument) {
		// java -jar gives:
		// sun.java.command=target/forkprocesstest-1.0-SNAPSHOT.jar
		// java .. mainclass gives:
		// sun.java.command=org.mahu.proto.forkprocesstest.App
		String javaClassPath = getSystemProperty("java.class.path");
		String fileSeperator = getSystemProperty("file.separator");
		String executable = getSystemProperty("java.home") + fileSeperator
				+ "bin" + fileSeperator + "java";
		List<String> command = new LinkedList<String>();
		command.add(executable);
		for (String option : jvmOptions) {
			command.add(option);
		}
		command.add("-cp");
		command.add(javaClassPath);
		command.add(mainClass.getName());
		if (argument != null) {
			command.add(argument);
		}
		return forkProcess(command);
	}

	public int forkProcess(final List<String> command) {
		checkNotNull(command);
		checkState(process == null);
		try {
			ProcessBuilder pb = new ProcessBuilder(command);
			// environment is copied from current process
			pb.command(command);
			synchronized (this) {
				process = pb.start();
				streamToChild = process.getOutputStream();
				outputData = collectChildData(System.out,
						process.getInputStream());
				errorData = collectChildData(System.err,
						process.getErrorStream());
				isRunning = true;
			}
			process.waitFor();
			exitValue = process.exitValue();
			return exitValue;
		} catch (InterruptedException | IOException e) {
			throw new ForkProcessException("Error cloning process", e);
		} finally {
			synchronized (this) {
				isRunning = false;
			}
		}
	}

	public synchronized boolean isProcessesStartedOrCompleted() {
		return process != null;
	}

	public synchronized boolean IsProcessesRunning() {
		return process != null && isRunning;
	}

	public void closeStreamToChild() {
		if (IsProcessesRunning()) {
			try {
				streamToChild.close();
			} catch (Exception e) {
				// ignore
			}
		}
	}

	public void destroyChild() {
		checkState(process != null);
		process.destroy();
	}

	public int getExitValue() {
		return exitValue;
	}

	public List<String> getOutputData() {
		return outputData.outputData;
	}

	public List<String> getErrorData() {
		return errorData.getOutputData();
	}

	private CollectChildOutputData collectChildData(
			final PrintStream printStream, final InputStream is) {
		CollectChildOutputData outputDataCollector = new CollectChildOutputData(
				printStream, is, printToConsole);
		(new Thread(outputDataCollector)).start();
		return outputDataCollector;
	}

	private String getSystemProperty(final String key) {
		String value = System.getProperty(key);
		checkNotNull(value);
		return value;
	}

	static class CollectChildOutputData implements Runnable {
		private final CountDownLatch cdl = new CountDownLatch(1);
		private final PrintStream printStream;
		private final InputStream is;
		private boolean printToConsole;
		final List<String> outputData = new LinkedList<String>();

		CollectChildOutputData(final PrintStream printStream,
				final InputStream is, final boolean printToConsole) {
			this.printStream = printStream;
			this.is = is;
			this.printToConsole = printToConsole;
		}

		@Override
		public void run() {
			try {
				BufferedReader r = new BufferedReader(new InputStreamReader(is));
				while (true) {
					String line = r.readLine();
					if (line == null) {
						break;
					}
					if (printToConsole) {
						printStream.println(line);
					} else {
						outputData.add(line);
					}
				}
			} catch (IOException e) {
				// expected, when peer closes stream;
			} finally {
				closeInputStream();
				synchronized (this) {
					cdl.countDown();
				}
			}
		}

		private List<String> getOutputData() {
			synchronized (this) {
				try {
					cdl.await(2000, TimeUnit.MILLISECONDS);
				} catch (InterruptedException e) {
					throw new ForkProcessException(
							"Failed to get data within set timeout", e);
				}
				return outputData;
			}
		}

		protected void closeInputStream() {
			try {
				is.close();
			} catch (IOException e) {
				LOGGER.warning("Failed to close inputstream from chld process.");
			}
		}

	}

}
