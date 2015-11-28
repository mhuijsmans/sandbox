package com.example.android.thekid;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class BluetoothService extends Service {
	private static final String TAG = "TheKid_Service";
	private static final boolean D = true;
	//
	private BluetoothConnectionService itsConnectionService;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public void onCreate() {
		// Called by the system when the Android service is first created
    if(D) Log.e(TAG, "onCreate()");
    itsConnectionService = 
    	new BluetoothConnectionService();
    itsConnectionService.setListener(
    			new BluetoothConnectionServiceListenerImpl(this, itsConnectionService));
    itsConnectionService.start();
	}

	public void onDestroy() {
		// Called by the system to notify a Service that it is no longer
		// used and is being removed.
		if(D) Log.e(TAG, "onDestroy()");
		itsConnectionService.stop();
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		// Called by the system every time a client explicitly starts the service
		// by calling startService(Intent), providing the arguments it supplied
		// and a unique integer token representing the start request.
		//
    // We want this service to continue running until it is explicitly
    // stopped, so return sticky.
		if(D) Log.e(TAG, "onStart()");
		// If Service is killed by OS and restarted, onStartCommand(..) is called with
		// a null intent.
		final String action = (intent!=null) ?
				intent.getAction() : Intents.START_BLUETOOTHSERVICE;		
		Log.d(TAG, "Received task Intent with action: " + action);

		if (action.equalsIgnoreCase(Intents.START_BLUETOOTHSERVICE)) {
			itsConnectionService.start();
		}
		//
		if (action.equalsIgnoreCase(Intents.STOP_BLUETOOTHSERVICE)) {
			itsConnectionService.stop();
		}
		//
		if (action.equalsIgnoreCase(Intents.END_BLUETOOTHCONNECTION)) {
			itsConnectionService.closeConnections();
		}			
		//
    return START_STICKY;
	}
}
