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

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.thekid.logging.MemoryLog;

/**
 * This is the main Activity that displays the current chat session.
 */
public class TheKid extends Activity {
	// Debugging
	private static final String TAG = "TheKid_Activity";
	private static final boolean D = true;
	// Creation message
	private final static String CREATE_MESSAGE = "Created 2010-12-23, 17:29";

	// Key names received from the BluetoothChatService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	// Layout Views
	private TextView itsTitle;
	private ListView itsMessageView;

	// Array adapter for the conversation thread
	private ArrayAdapter<String> itsMessageArrayAdapter;
	// Local Bluetooth adapter
	private BluetoothAdapter itsBluetoothAdapter = null;

	// Member object for the chat services
	// private BluetoothConnectionService mChatService = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (D)
			Log.e(TAG, "onCreate()");

		// Set up the window layout
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.custom_title);

		// Set up the custom title
		itsTitle = (TextView) findViewById(R.id.title_left_text);
		itsTitle.setText(R.string.app_name);
		itsTitle = (TextView) findViewById(R.id.title_right_text);

		// Get local Bluetooth adapter
		itsBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		// If the adapter is null, then Bluetooth is not supported
		if (itsBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG)
					.show();
			finish();
			return;
		}
		//
		MemoryLog.init(50);
		MemoryLog.add(CREATE_MESSAGE);		
	}

	@Override
	public void onStart() {
		super.onStart();
		if (D)
			Log.e(TAG, "onStart()");

		// If BT is not on, request that it be enabled by sending an Intent
		// to the BluetoothAdapter. That will open a new window.
		if (!itsBluetoothAdapter.isEnabled()) {
			final Intent enableRequestIntent = 
				new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivity(enableRequestIntent);
		} else {
			BluetoothServiceHelper.startBluetoothService(this);
		}
		//
		// Initialize the array adapter
		itsMessageArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);
		itsMessageView = (ListView) findViewById(R.id.in);
		itsMessageView.setAdapter(itsMessageArrayAdapter);
	}

	@Override
	public synchronized void onResume() {
		super.onResume();
		if (D)
			Log.e(TAG, "onResume()");
		SystemClock.sleep(1000);
		refreshLogView();
	}

	@Override
	public synchronized void onPause() {
		super.onPause();
		if (D)
			Log.e(TAG, "onPause()");
	}

	@Override
	public void onStop() {
		super.onStop();
		if (D)
			Log.e(TAG, "onStop()");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (D)
			Log.e(TAG, "onDestroy()");
	}

	private void ensureDiscoverable() {
		if (D)
			Log.d(TAG, "ensure discoverable");
		if (itsBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoverableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
					300);
			startActivity(discoverableIntent);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.option_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.discoverable:
			// Ensure this device is discoverable by others
			ensureDiscoverable();
			return true;
		case R.id.startService:
			// restart the service
			BluetoothServiceHelper.startBluetoothService(this);
			// It is unclear what a good value is. This depends on Bluetooth.
			SystemClock.sleep(1000);
			refreshLogView();
			return true;
		case R.id.stopService:
			// restart the service
			BluetoothServiceHelper.stopBluetoothService(this);
			SystemClock.sleep(1000);
			refreshLogView();
			return true;			
		case R.id.refresh:
			// refresh the logview
			refreshLogView();
			return true;
		case R.id.endConnection:
			BluetoothServiceHelper.endBluetoothConnection(this);
			refreshLogView();
			return true;			
		}
		return false;
	}

	private void refreshLogView() {
		itsMessageArrayAdapter.clear();
		for (String s : MemoryLog.getRows()) {
			itsMessageArrayAdapter.add(s);
		}
	}
	
	// In the old implementation in onStart() the method startActivityForResult(..) 
	// was used instead of startActivity().
	// 
	// private static final int REQUEST_ENABLE_BT = 1;
	// startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
	//
	// Using startActivityForResult results in onActivityResult() to be called with the result.
	//
	// Using startActivity() doesn't return a result.
	// Instead the BroadcastReceiver is used, which is a better strategy, as that receives
	// notifications when Bluetooth comes alive and dies. Based on that an Intent
	// can be send to the BluetoothService to start / stop services.
	
	/*
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (D)
			Log.d(TAG, "onActivityResult " + resultCode);
		switch (requestCode) {
		case REQUEST_ENABLE_BT:
			// When the request to enable Bluetooth returns
			if (resultCode == Activity.RESULT_OK) {
				// Bluetooth is now enabled, so starts the service
				BluetoothServiceHelper.startBluetoothService(this);
			} else {
				// User did not enable Bluetooth or an error occured
				Log.d(TAG, "BT not enabled");
				Toast.makeText(this, R.string.bt_not_enabled_leaving,
						Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}
	*/

}