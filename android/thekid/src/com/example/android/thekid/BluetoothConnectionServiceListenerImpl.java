package com.example.android.thekid;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;

public class BluetoothConnectionServiceListenerImpl implements
		BluetoothConnectionServiceListener {
	private static final String TAG = "TheKidListener2";
	private static final boolean D = true;	
	private Context itsContext;
	private final BluetoothConnectionService itsBTConnectionService;
	
	BluetoothConnectionServiceListenerImpl(Context context,
			BluetoothConnectionService bluetoothConnectionService) {
		itsContext = context;
		itsBTConnectionService = bluetoothConnectionService;
	}

	public void stateChanged(int newState) {
	}

	public void deviceConnected(BluetoothDevice device) {
	}

	public void unableToConnectedToDevice() {
	}

	public void connectionLost() {
	}

	public void handleReceivedData(String key, byte[] b, int length) {
    final String msg = new String(b, 0, length);
    if(D) Log.e(TAG, "handleReceivedData(): "+msg);    
    if (msg.startsWith("call:")) {
    	PhoneManager.callNumber(itsContext, msg.substring(5));
    }	else 
    if (msg.startsWith("ping")) {
    	itsBTConnectionService.write(key, "pong".getBytes());
    }	    
	}

	public void handleSendData(byte[] b) {
	}

	public void handleLogMsg(final String s) {
	}
}
