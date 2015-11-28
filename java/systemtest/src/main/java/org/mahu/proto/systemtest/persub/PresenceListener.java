package org.mahu.proto.systemtest.persub;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PresenceListener implements Callable<Void> {

	public final static int PRESENCE_PORT = 12345;

	private static final Logger log = Logger.getLogger(PresenceListener.class
			.getName());

	private SortedSet<String> msgs = new TreeSet<String>();

	public class PresenceReporter implements Callable<Void> {
		@Override
		public Void call() {
			log.info("ENTER");
			try {
			while (true) {
				TimeUnit.SECONDS.sleep(3);
				SortedSet<String> tmp = getMsgsAndClearList();
				StringBuilder sb = new StringBuilder();
				boolean isFirst = true;
				for (String msg : tmp) {
					if (!isFirst) {
						sb.append("-");
					}
					isFirst = false;
					sb.append(msg);
				}
				log.info("Notifications from: " + sb.toString());
			}
			} catch (InterruptedException e) {
				log.info("interrupted");
			} finally {
				log.info("LEAVE");
			}
			return null;
		}
	}

	public PresenceReporter getPresenceReporter() {
		return new PresenceReporter();
	}

	@Override
	public Void call() {
		startPresenceService();
		return null;
	}

	private void startPresenceService() {
		log.info("ENTER");
		DatagramSocket serverSocket = null;
		try {
			serverSocket = new DatagramSocket(PRESENCE_PORT);
			byte[] buffer = new byte[1024];
			while (true) {
				DatagramPacket packet = new DatagramPacket(buffer,
						buffer.length);
				serverSocket.receive(packet);
				String sentence = StringCoder.decodeUTF8(packet.getData(), 0, packet.getLength());
				addToMsgList(sentence);
				log.fine("RECEIVED: " + sentence);
			}
		} catch (SocketException e) {
			log.log(Level.WARNING,
					"SocketException (e.g. socket closed): " + e.getMessage());
		} catch (IOException e) {
			log.log(Level.WARNING, "IOException: " + e.getMessage());
		} finally {
			close(serverSocket);
			log.info("LEAVE");
		}
	}

	private synchronized void addToMsgList(String sentence) {
		msgs.add(sentence);
	}

	private synchronized SortedSet<String> getMsgsAndClearList() {
		SortedSet<String> tmp = msgs;
		msgs = new TreeSet<String>();
		return tmp;
	}

	protected void close(DatagramSocket serverSocket) {
		if (serverSocket != null) {
			serverSocket.close();
		}
	}
}
