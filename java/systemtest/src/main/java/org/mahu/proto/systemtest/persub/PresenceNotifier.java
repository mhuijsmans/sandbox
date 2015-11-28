package org.mahu.proto.systemtest.persub;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PresenceNotifier implements Callable<Void> {

	public final static int PRESENCE_PORT = 12345;

	private static final Logger log = Logger.getLogger(PresenceNotifier.class
			.getName());

	private final String msg;

	public PresenceNotifier(final String msg) {
		this.msg = msg;
	}

	@Override
	public Void call() {
		startPresenceNotification();
		return null;
	}

	private void startPresenceNotification() {
		log.info("ENTER");
		DatagramSocket clientSocket = null;
		try {
			clientSocket = new DatagramSocket();
			InetAddress IPAddress = InetAddress.getByName("localhost");
			byte[] sendData = StringCoder.encodeUTF8(msg);
			DatagramPacket sendPacket = new DatagramPacket(sendData,
					sendData.length, IPAddress, PresenceListener.PRESENCE_PORT);
			while (true) {
				clientSocket.send(sendPacket);
				TimeUnit.SECONDS.sleep(1);
			}
		} catch (SocketException e) {
			log.log(Level.WARNING, "SocketException: " + e.getMessage());
		} catch (UnknownHostException e) {
			log.log(Level.WARNING, "UnknownHostException: " + e.getMessage());
		} catch (IOException e) {
			log.log(Level.WARNING, "IOException: " + e.getMessage());
		} catch (InterruptedException e) {
			log.log(Level.INFO, "InterruptedException");
		} finally {
			close(clientSocket);
			log.info("LEAVE");
		}

	}

	protected void close(DatagramSocket clientSocket) {
		if (clientSocket != null) {
			clientSocket.close();
		}
	}
}
