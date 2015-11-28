package com.example.android.thekid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class PhoneManager {
	public static void callNumber(Context context, String number) {
		try {
			Intent intent = new Intent(Intent.ACTION_CALL);
			intent.setData(Uri.parse("tel:" + number));
			if (context instanceof Activity == false) {
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			}
			context.startActivity(intent);
		} catch (Exception e) {
			Log.e("pirasa", "Failed to invoke call", e);
		}
	}

	public static void sendSMS(Context context, String number) {
		try {
			Intent sendIntent = new Intent(Intent.ACTION_VIEW);
			sendIntent.putExtra("address", number);
			sendIntent.setType("vnd.android-dir/mms-sms");
			if (context instanceof Activity == false) {
				sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			}
			context.startActivity(sendIntent);
		} catch (Exception e) {
			Log.e("pirasa", "Failed to invoke call", e);
		}
	}

	public static void sendEmail(Activity context, String email) {
		final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.setType("plain/text");
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
				new String[] { email });
		if (context instanceof Activity == false) {
			emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}
		context.startActivity(emailIntent);
	}

}
