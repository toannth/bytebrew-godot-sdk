package com.bytebrew.bytebrewlibrary;

import android.util.Log;
import org.json.JSONObject;

class BytePushConfigs {
	public static BytePushConfigs _instance = null;

	private String pushToken;

	private String applicationID;

	private String apiKey;

	private String gameID;

	private String sdkKey;

	private String userID;

	public static BytePushConfigs getInstance() {
		if (_instance == null)
			_instance = new BytePushConfigs();
		return _instance;
	}

	public void SetPushToken(String token) {
		this.pushToken = token;
	}

	public String GetPushToken() {
		return this.pushToken;
	}

	public void SetAppConfig(JSONObject appConfig) {
		try {
			this.sdkKey = appConfig.getString("key");
			this.gameID = appConfig.getString("game_id");
			this.userID = appConfig.getString("user_id");
		} catch (Exception e) {
			Log.d("ByteBrew Push", "Could not set app configs: " + e.getMessage());
		}
	}

	public String GetSDKKey() {
		return this.sdkKey;
	}

	public String GetUserID() {
		return this.userID;
	}

	public String GetGameID() {
		return this.gameID;
	}
}
