package com.bytebrew.bytebrewlibrary;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import org.json.JSONObject;

class ByteBrewPushNotifications {
	private static ByteBrewFCMInitializer byteBrewFCMInitializer;

	private static Context appContext;

	private static ByteBrewUtils byteBrewUtils;

	private static boolean isPushNotificationsSetup = false;

	private static boolean pushTokenSetup = false;

	private static boolean byteBrewSDKIsInitialized = false;

	private static String sdkKey;

	private static String gameID;

	private static String userID;

	public static void StartByteBrewPushNotifications(final Context context) {
		appContext = context;
		Intent openedIntent = ((Activity) appContext).getIntent();
		Log.d("ByteBrew Push Launched", "Intent: " + openedIntent.hasExtra("byte_message_sent_id"));
		byteBrewUtils = new ByteBrewUtils();
		JSONObject appConfig = byteBrewUtils.GetAppConfig(appContext);
		if (!appConfig.has("key")) {
			Log.d("ByteBrew Push", "Can't setup push notifications");
			return;
		}
		BytePushConfigs.getInstance().SetAppConfig(appConfig);
		try {
			sdkKey = appConfig.getString("key");
			gameID = appConfig.getString("game_id");
			userID = appConfig.getString("user_id");
		} catch (Exception e) {
			Log.d("ByteBrew Push", "Can't setup push notifications");
			return;
		}
		RequestPushNotificationPermissions(context);
		isPushNotificationsSetup = true;
		(new BytePushHTTPManager(gameID, sdkKey, userID)).GetPushConfigs(new PushTaskExecuted() {
			public void onTaskDone(String data) {
				if (data.length() == 0) {
					Log.d("ByteBrew Push", "No configs, can't setup push notifications");
					return;
				}
				ByteBrewPushNotifications.byteBrewFCMInitializer = new ByteBrewFCMInitializer();
				try {
					JSONObject config = new JSONObject(data);
					ByteBrewPushNotifications.byteBrewFCMInitializer.StartFCM(context, config);
					ByteBrewPushNotifications.CreateNotificationChannel(context);
				} catch (Exception e) {
					Log.d("ByteBrew Push", e.getMessage());
				}
			}
		});
		CheckIntentForOpenData(openedIntent);
	}

	private static void RequestPushNotificationPermissions(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences("bytebrew_prefs", 0);
		if (Build.VERSION.SDK_INT >= 33) {
			boolean hasRequested = sharedPreferences.getBoolean("push_perms_requested", false);
			if (ContextCompat.checkSelfPermission(context, "android.permission.POST_NOTIFICATIONS") != 0
					&& !hasRequested) {
				ActivityCompat.requestPermissions((Activity) context,
						new String[] { "android.permission.POST_NOTIFICATIONS" }, 1);
				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.putBoolean("push_perms_requested", true);
				editor.apply();
			}
		}
	}

	private static void CreateNotificationChannel(Context context) {
		if (Build.VERSION.SDK_INT >= 26) {
			CharSequence name = context.getApplicationInfo().loadLabel(context.getPackageManager()).toString()
					+ " Notifications";
			String description = "Push Notifications";
			int importance = 4;
			NotificationChannel channel = new NotificationChannel(context.getPackageName() + ".pushnotifications", name,
					importance);
			channel.setDescription(description);
			channel.setLockscreenVisibility(1);
			NotificationManager notificationManager = (NotificationManager) context
					.getSystemService(NotificationManager.class);
			notificationManager.createNotificationChannel(channel);
		}
	}

	public static String GetPushToken() {
		return BytePushConfigs.getInstance().GetPushToken();
	}

	public static boolean ArePushNotificationsSetup() {
		return isPushNotificationsSetup;
	}

	public static void SetPushTokenSetup() {
		pushTokenSetup = true;
		if (byteBrewSDKIsInitialized)
			UpdatePushToken();
	}

	public static void UpdatePushToken() {
		if (pushTokenSetup)
			(new BytePushHTTPManager(gameID, sdkKey, userID))
					.UpdatePushToken(BytePushConfigs.getInstance().GetPushToken());
	}

	public static void ByteBrewSDKInitialized() {
		byteBrewSDKIsInitialized = true;
		if (isPushNotificationsSetup)
			UpdatePushToken();
	}

	private static void CheckIntentForOpenData(Intent openedIntent) {
		if (openedIntent == null)
			return;
		if (openedIntent.hasExtra("byte_message_sent_id")) {
			ByteBrewHandler.getInstance();
			openedIntent.putExtra("session_id", ByteBrewHandler.sessionID);
			(new BytePushHTTPManager(gameID, sdkKey, userID)).UpdatePushStats(openedIntent);
		}
	}
}
