/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.thekid;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import com.example.android.thekid.logging.MemoryLog;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.SystemClock;
import android.util.Log;

// This class handles following 
// - it has a thread that listens for incoming connections, 
// - and a thread for performing data transmissions when connected.
public class BluetoothConnectionService {
	// Debugging
	private static final String TAG = "TheKid_BTConnectionService";
	private static final boolean D = false;

	// Bluetooth Service Discovery Protocol (SDP) related properties.
	// Name for the SDP record when creating server socket
	private static final String SDP_NAME = "BluetoothKidService";
	// Unique UUID for the SDP record.
	// This UUID is also to be used by remote application
	private static final UUID UUID_SDP_RECORD = UUID
			.fromString("089cecf3-0f1f-4eb9-99cb-7d55e993a45d");
	//
	private final static AtomicInteger theirCntr = new AtomicInteger();

	// Instance data
	private final BluetoothAdapter itsBTAdapter;
	private ServerSocketThread itsListeningThread;
	// next line is probably not needed, because unlike TCP, only a single connection is allowed / socket 
	private HashMap<String, ConnectionThread> itsConnections = new HashMap<String, ConnectionThread>();
	private BluetoothConnectionServiceListener itsListener;

	public BluetoothConnectionService() {
		itsBTAdapter = BluetoothAdapter.getDefaultAdapter();
	}

	public void setListener(BluetoothConnectionServiceListener listener) {
		itsListener = listener;
	}

	/**
	 * Start the Listening thread.
	 */
	public synchronized void start() {
		// Start the thread to listen on a BluetoothServerSocket
		if (itsListeningThread == null) {
			itsListeningThread = new ServerSocketThread();
			itsListeningThread.start();
		}
	}

	/**
	 * Start a new ConnectionThread for the new Bluetooth connection
	 * 
	 * @param socket
	 *          The BluetoothSocket on which the connection was made
	 * @param device
	 *          The BluetoothDevice that has been connected
	 */
	public synchronized void connected(final BluetoothSocket socket,
			final BluetoothDevice device) {
		if (itsListeningThread == null) {
			return;
		}
		// Start the thread to manage the connection and perform transmissions
		ConnectionThread tmp = new ConnectionThread(socket);
		itsConnections.put(tmp.getKey(), tmp);
		tmp.start();

		// Send the name of the connected device back to the UI Activity
		itsListener.deviceConnected(device);
	}

	public void write(final String keyConnectionThread, final byte[] out) {
		ConnectionThread tmp;
		synchronized (this) {
			tmp = itsConnections.get(keyConnectionThread);
		}
		if (tmp != null) {
			tmp.write(out);
		}
	}

	/**
	 * Stop all threads
	 */
	public synchronized void stop() {
		if (itsListeningThread != null) {
			itsListeningThread.cancel();
			itsListeningThread = null;
			//
			closeConnections();
		}
	}

	public synchronized void closeConnections() {
		for (ConnectionThread tmp : itsConnections.values()) {
			tmp.cancel();
		}
	}

	synchronized void connectionEnded(final ConnectionThread thread) {
		itsConnections.remove(thread.getKey());
	}

	// =========================================================================

	/**
	 * This thread open a server socket and waits for incoming connections. After
	 * a incoming connection is received, it will again wait. If Bluetooth is
	 * disabled, will will periodically try to reopen the server socket.
	 */
	enum State1 {
		INIT, LISTENING, TIMER, CANCELLING, EXIT
	};

	private class ServerSocketThread extends Thread {
		// The server socket
		private BluetoothServerSocket itsServerSocket = null;
		private boolean itsIsCancelled = false;
		private State1 itsState = State1.INIT;
		private final static String LOGNAME = "ServerSocket";

		ServerSocketThread() {
		}

		public void run() {
			try {
				setName("AcceptThread");
				BluetoothSocket socket = null;

				while (!itsIsCancelled) {
					obtainedServerSocket();
					try {
						// This is a blocking call and will only return on a
						// successful connection or an IOException
						// When turning off bluetooth, the accept() will receive a
						// IOException and socket is null.
						setState(State1.LISTENING);
						socket = null;
						if (itsServerSocket != null) {
							socket = itsServerSocket.accept();
							// Situation normal. Start the connected thread.
							connected(socket, socket.getRemoteDevice());
						}
					} catch (IOException e) {
						Log.d(TAG, "ServerSocketThread accept() failed", e);
						return;
					}
					// No socket is a signal that the ServerSocket was closed().
					// We need a notification when Bluetooth is available again.
					// This is handled via the BluetoothBroadcastReceiver
					//
					// Note that there probably Bluetooth is slow / user is slow,
					// so there is a small risk that the Blueooth On is received 
					// before the off is processed and completed. This is ok for now.

				}
			} finally {
				setState(State1.EXIT);
			}
		}

		void cancel() {
			// Important, so that socket is closed, when Thread is cancelled
			itsIsCancelled = true;			
			setState(State1.CANCELLING);
			closeServerSocket();
		}

		private void setState(State1 newState) {
			if (itsState != newState) {
				MemoryLog.add(LOGNAME + " state=" + newState);
				itsState = newState;
			}
		}

		private void obtainedServerSocket() {
			if (itsServerSocket == null) {
				BluetoothServerSocket tmp = null;
				// Create a new listening server socket
				try {
					// The system will also register a Service Discovery Protocol (SDP)
					// record with the local SDP server containing the specified UUID,
					// service name, and auto-assigned channel. Remote Bluetooth devices
					// can use the same UUID to query our SDP server and discover which
					// channel to connect to.
					// This SDP record will be removed when this socket is closed, or if
					// this application closes unexpectedly.
					tmp = itsBTAdapter.listenUsingRfcommWithServiceRecord(SDP_NAME,
							UUID_SDP_RECORD);
				} catch (IOException e) {
					; // ignore
				}
				itsServerSocket = tmp;
			}
		}

		private void closeServerSocket() {
			try {
				if (itsServerSocket != null) {
					itsServerSocket.close();
				}
			} catch (IOException e) {
				;
			} finally {
				itsServerSocket = null;
			}
		}
	}

	// =========================================================================

	/**
	 * This thread runs during a connection with a remote device. It handles all
	 * incoming and outgoing transmissions.
	 */
	enum State2 {
		INIT, CONNECTED, EXIT
	};

	private class ConnectionThread extends Thread {
		private final String itsKey = ConnectionThread.class.getSimpleName() + "-"
				+ theirCntr.getAndIncrement();
		private final BluetoothSocket itsSocket;
		private final InputStream itsInputStream;
		private final OutputStream itsOutputStream;
		private State2 itsState = State2.INIT;
		private final static String LOGNAME = "Socket";

		ConnectionThread(BluetoothSocket socket) {
			itsSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;

			// Get the BluetoothSocket input and output streams
			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e) {
				Log.e(TAG, "temp sockets not created", e);
				closeSocket();
				tmpIn = null;
				tmpOut = null;
			}
			itsInputStream = tmpIn;
			itsOutputStream = tmpOut;
		}

		String getKey() {
			return itsKey;
		}

		public void run() {
			setState(State2.CONNECTED);
			try {
				byte[] buffer = new byte[1024];
				int bytes;
				// Keep listening to the InputStream while connected
				while (true) {
					try {
						// Read from the InputStream
						bytes = itsInputStream.read(buffer);
						itsListener.handleReceivedData(itsKey, buffer, bytes);
					} catch (IOException e) {
						Log.e(TAG, getKey() + ": disconnected", e);
						break;
					}
				}
			} finally {
				setState(State2.EXIT);
				closeSocket();
				BluetoothConnectionService.this.connectionEnded(this);
			}
		}

		/**
		 * Write to the connected OutStream.
		 * 
		 * @param buffer
		 *          The bytes to write
		 */
		void write(byte[] buffer) {
			try {
				itsOutputStream.write(buffer);
			} catch (IOException e) {
				; // ignore
			}
		}

		void cancel() {
			closeSocket();
		}

		private synchronized void closeSocket() {
			try {
				itsSocket.close();
			} catch (Exception e) {
				; // ignore
			}
		}

		private void setState(State2 newState) {
			if (itsState != newState) {
				MemoryLog.add(LOGNAME + " state=" + newState);
				itsState = newState;
			}
		}
	}
}
