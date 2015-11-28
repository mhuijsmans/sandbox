package com.example.android.thekid;

import android.content.Context;
import android.content.Intent;

/**
 * Helper class90
 */
public class BluetoothServiceHelper {

	public static void startBluetoothService(Context ctx) {		
		Intent i = createIntent(ctx,Intents.START_BLUETOOTHSERVICE);
		ctx.startService(i);		
	}
	
	public static void stopBluetoothService(Context ctx) {		
		Intent i = createIntent(ctx,Intents.STOP_BLUETOOTHSERVICE);
		ctx.startService(i);		
	}	
	
	public static void endBluetoothConnection(Context ctx) {		
		Intent i = createIntent(ctx,Intents.END_BLUETOOTHCONNECTION);
		ctx.startService(i);		
	}	
	
	private static Intent createIntent(Context ctx, String action) {
		Intent i = new Intent(ctx, BluetoothService.class);
		i.setAction(action);
		return i;
	}
}