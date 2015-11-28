package com.example.android.thekid;

import com.example.android.thekid.logging.MemoryLog;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

// used the following info to implement this class
// http://www.tutorialforandroid.com/2009/01/get-phone-state-when-someone-is-calling_22.html

public class BluetoothBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action
				.equalsIgnoreCase("android.bluetooth.adapter.action.STATE_CHANGED")) {
			final int newState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
					BluetoothAdapter.STATE_OFF);
			MemoryLog.add("BBR: " + action + " newState=" + getState(newState));
			switch (newState) {
			case BluetoothAdapter.STATE_TURNING_OFF:
				// Pre-information that bluetooth is going to be cleared
					BluetoothServiceHelper.stopBluetoothService(context);
				break;
			case BluetoothAdapter.STATE_ON:
				// This states indicates that the local Bluetooth adapter is on, and
				// ready for use.
				BluetoothServiceHelper.startBluetoothService(context);
			}
		}
	}

	private String getState(int state) {
		if (state == BluetoothAdapter.STATE_ON)
			return "STATE_ON";
		if (state == BluetoothAdapter.STATE_OFF)
			return "STATE_OFF";
		if (state == BluetoothAdapter.STATE_TURNING_ON)
			return "STATE_TURNING_ON";
		if (state == BluetoothAdapter.STATE_TURNING_OFF)
			return "STATE_TURNING_OFF";
		return "" + state;

	}

}
