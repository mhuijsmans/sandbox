package com.example.android.thekid;

import android.bluetooth.BluetoothDevice;

public interface BluetoothConnectionServiceListener {
	
	public void stateChanged(int newState);
	
	public void deviceConnected(BluetoothDevice device);
	
	public void unableToConnectedToDevice();
	
	public void connectionLost();
	
	public void handleReceivedData(String key, byte[]b, int length);
}
