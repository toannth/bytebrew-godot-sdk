package com.bytebrew.bytebrewlibrary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ByteBrewBasicFCMBroadcastReceiver extends BroadcastReceiver {
	public void onReceive(Context context, Intent intent) {
		try {
			if (intent.hasExtra("byte_message_sent_id")) {
				intent.putExtra("pushIntent", "RECEIVED");
				ByteBrewPushService.enqueueWork(context, intent);
				abortBroadcast();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
}
