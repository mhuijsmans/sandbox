package org.mahu.tools.syslogservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public final class SysLogServerConsole implements AutoCloseable {

    private static final long DEFAULT_CONNECTION_TIMEOUT_MS = 10*1000;
    
	private final int listenPort;
    private ServerSocket listenSocket;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public SysLogServerConsole(final int listenPort) {
        this.listenPort = listenPort;
    }

    public Future<?> start() throws IOException {
        try {
            listenSocket = new ServerSocket(listenPort);
        } catch (IOException e) {
        	Logger.log("Failed to start listering server on port="+listenPort+" because of exception="+e.getMessage());
            throw e;
        }

        return executorService.submit(() -> {
            try {
                while (!listenSocket.isClosed()) {
                    try (final Socket connectionSocket = listenSocket.accept();
                            final InputStreamReader inputStreamReader = new InputStreamReader(
                                    connectionSocket.getInputStream());
                            final BufferedReader inFromClient = new BufferedReader(inputStreamReader)) {
                    	// for each received line logger is called
                        inFromClient.lines().forEach(Logger::syslogline);
                    }
                    //no more data on socket. wait for next connection
                }
            } catch (final IOException e) {
            	Logger.log("Exception while in operation, exception="+e.getMessage());
            }
        });
    }
    
    @Override
    public void close() {
        try {
            executorService.shutdown();
            executorService
                    .awaitTermination(DEFAULT_CONNECTION_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }    
    
}
