package com.bytebrew.bytebrewlibrary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ByteBrewDismissPushReceiver extends BroadcastReceiver {
	public void onReceive(Context context, Intent intent) {
		if (intent.hasExtra("byte_message_sent_id")) {
			Log.d("ByteBrewDismissReceiver", "Dismissed Notification Event");
			intent.putExtra("pushIntent", "DISMISSED");
			ByteBrewPushService.enqueueWork(context, intent);
		}
	}
}
