package com.bytebrew.bytebrewlibrary;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import org.json.JSONObject;

public class ByteBrewPushService extends JobIntentService {
	static void enqueueWork(Context context, Intent work) {
		enqueueWork(context, ByteBrewPushService.class, 1234, work);
	}

	protected void onHandleWork(@NonNull Intent intent) {
		Log.d("ByteBrew Push", "Handling Notification");
		ByteBrewUtils byteBrewUtils = new ByteBrewUtils();
		JSONObject appConfig = byteBrewUtils.GetAppConfig((Context) this);
		if (!appConfig.has("key")) {
			Log.d("ByteBrew Push", "Can't setup push notifications");
			return;
		}
		BytePushConfigs.getInstance().SetAppConfig(appConfig);
		if (intent.getStringExtra("pushIntent").equals("RECEIVED")) {
			HandleReceivedPushMessage(intent);
		} else if (intent.getStringExtra("pushIntent").equals("DISMISSED")) {
			HandleDismissedPushMessage(intent);
		} else {
			Log.d("ByteBrew Push", "Category doesn't match");
		}
	}

	public void HandleReceivedPushMessage(Intent intent) {
		try {
			Log.d("ByteBrew Push", "Notification was received");
			if (intent.hasExtra("byte_message_type")) {
				if (intent.getStringExtra("byte_message_type").equals("uninstall_silent"))
					(new BytePushHTTPManager(BytePushConfigs.getInstance().GetGameID(),
							BytePushConfigs.getInstance().GetSDKKey(), BytePushConfigs.getInstance().GetUserID()))
							.UpdateUninstallStats(intent);
			} else {
				if (ByteBrewApplcationCallbacks.isInForeground)
					return;
				(new BytePushHTTPManager(BytePushConfigs.getInstance().GetGameID(),
						BytePushConfigs.getInstance().GetSDKKey(), BytePushConfigs.getInstance().GetUserID()))
						.UpdatePushStats(intent);
				ShowPushMessage(getApplicationContext(),
						new JSONObject(ByteBundleToJson.getJson(intent.getExtras(), 0)));
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public void HandleDismissedPushMessage(Intent intent) {
		try {
			Log.d("ByteBrew Push", "Notification was dismissed");
			(new BytePushHTTPManager(BytePushConfigs.getInstance().GetGameID(),
					BytePushConfigs.getInstance().GetSDKKey(), BytePushConfigs.getInstance().GetUserID()))
					.UpdatePushStats(intent);
			RemoveAnyPushNotificationMatching(intent.getIntExtra("push_request_id", 0));
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public void ShowPushMessage(Context context, JSONObject pushMessage) {
		try {
			int requestID = (int) System.currentTimeMillis();
			SaveNotification(requestID, pushMessage.getString("byte_message_sent_id"));
			Intent intent = context.getApplicationContext().getPackageManager()
					.getLaunchIntentForPackage(context.getApplicationContext().getPackageName());
			intent.setFlags(268468224);
			intent.putExtra("byte_message_sent_id", pushMessage.getString("byte_message_sent_id"));
			intent.putExtra("push_request_id", requestID);
			intent.putExtra("pushIntent", "OPENED");
			PendingIntent pendingIntent = CreatePendingIntentGetActivity(context.getApplicationContext(), requestID,
					intent);
			Intent dismissIntent = new Intent(context, ByteBrewDismissPushReceiver.class);
			dismissIntent.putExtra("byte_message_sent_id", pushMessage.getString("byte_message_sent_id"));
			dismissIntent.putExtra("push_request_id", requestID);
			PendingIntent pendingDismissIntent = CreatePendingIntentGetBroadCast(context.getApplicationContext(),
					requestID, dismissIntent);
			NotificationCompat.Builder builder = (new NotificationCompat.Builder(context,
					context.getPackageName() + ".pushnotifications")).setSmallIcon((context.getApplicationInfo()).icon)
					.setContentTitle(pushMessage.getString("gcm.notification.title"))
					.setContentText(pushMessage.getString("gcm.notification.body")).setPriority(1)
					.setContentIntent(pendingIntent).setDeleteIntent(pendingDismissIntent).setAutoCancel(true);
			if (pushMessage.has("byte_sm_icon")) {
				int smallid = context.getResources().getIdentifier(pushMessage.getString("byte_sm_icon"), "drawable",
						context.getPackageName());
				if (smallid != 0)
					builder.setSmallIcon(smallid);
			}
			if (pushMessage.has("byte_lg_icon")) {
				int largeid = context.getResources().getIdentifier(pushMessage.getString("byte_lg_icon"), "drawable",
						context.getPackageName());
				if (largeid != 0) {
					Bitmap lgIcon = BitmapFactory.decodeResource(context.getResources(), largeid);
					builder.setLargeIcon(lgIcon);
				}
			}
			NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
			notificationManager.notify(pushMessage.getInt("google.sent_time"), builder.build());
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public static PendingIntent CreatePendingIntentGetActivity(Context context, int requestID, Intent intent) {
		int flags = (Build.VERSION.SDK_INT >= 23) ? 67108864 : 0;
		return PendingIntent.getActivity(context, requestID, intent, flags);
	}

	public static PendingIntent CreatePendingIntentGetBroadCast(Context context, int requestID, Intent intent) {
		int flags = (Build.VERSION.SDK_INT >= 23) ? 67108864 : 0;
		return PendingIntent.getBroadcast(context, requestID, intent, flags);
	}

	private void SaveNotification(int requestID, String pushID) {
		SharedPreferences sharedPreferences = getSharedPreferences("bytebrew_push_prefs", 0);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString("notification." + requestID, pushID);
		editor.apply();
	}

	private void RemoveAnyPushNotificationMatching(int requestID) {
		SharedPreferences sharedPreferences = getSharedPreferences("bytebrew_push_prefs", 0);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.remove("notification." + requestID);
		editor.apply();
	}
}
