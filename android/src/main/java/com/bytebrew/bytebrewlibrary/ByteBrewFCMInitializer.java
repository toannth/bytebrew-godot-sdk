package com.bytebrew.bytebrewlibrary;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.json.JSONObject;

public class ByteBrewFCMInitializer {
	FirebaseApp firebaseApp;

	public void StartFCM(Context context, JSONObject config) {
		try {
			Log.d("ByteBrew FCM", "starting fcm initialization");
			String applicationID = config.getString("app-id");
			String apiKey = config.getString("api-key");
			String senderID = config.getString("sender-id");
			String projectID = config.getString("project-id");
			try {
				FirebaseApp.getInstance();
				Log.d("ByteBrew FCM Init", "Is Already Initialized");
				GetToken();
			} catch (Exception e) {
				Log.d("ByteBrew FCM Init", "Not Already Initialized");
				this.firebaseApp = FirebaseApp.initializeApp(context, (new FirebaseOptions.Builder())
						.setApplicationId(applicationID)
						.setApiKey(apiKey)
						.setGcmSenderId(senderID)
						.setProjectId(projectID)
						.build());
				GetToken();
			}
		} catch (Exception e) {
			Log.d("ByteBrew FCM Exception", e.getMessage());
		}
	}

	public void GetToken() {
		try {
			FirebaseApp.getInstance();
			Log.d("ByteBrew FCM Init", "Is Initialized");
		} catch (Exception e) {
			Log.d("ByteBrew FCM Init", "Not Initialized");
			return;
		}
		Log.d("ByteBrew FCM", "Getting Push Token");
		FirebaseMessaging messaging = (FirebaseMessaging) FirebaseApp.getInstance().get(FirebaseMessaging.class);
		messaging.getToken().addOnCompleteListener(new OnCompleteListener<String>() {
			public void onComplete(@NonNull Task<String> task) {
				if (!task.isSuccessful()) {
					Log.d("ByteBrew", "Fetching FCM registration token failed", task.getException());
					return;
				}
				String token = (String) task.getResult();
				BytePushConfigs.getInstance().SetPushToken(token);
				ByteBrewPushNotifications.SetPushTokenSetup();
			}
		});
	}
}
