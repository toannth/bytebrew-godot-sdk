package com.bytebrew.bytebrewlibrary;

import android.content.Intent;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.json.JSONObject;

class BytePushHTTPManager {
	private ExecutorService taskExecuter;

	protected boolean isPushUpdate = false;

	protected boolean isStatsTracker = false;

	protected boolean isSettingsRetrieval = false;

	protected String sdkKey;

	protected String gameID;

	protected String userID;

	protected static final String _pushtokenurl = "https://push-api.bytebrew.io/push/services/token";

	protected static final String _pushstatsurl = "https://push-api.bytebrew.io/push/services/logs";

	protected static final String _pushuninstallurl = "https://push-api.bytebrew.io/push/services/uninstall";

	protected static final String _pushsettingsurl = "https://push-api.bytebrew.io/push/services/config";

	public BytePushHTTPManager(String gameID, String key, String userID) {
		this.taskExecuter = Executors.newSingleThreadExecutor();
		this.sdkKey = key;
		this.gameID = gameID;
		this.userID = userID;
		this.isPushUpdate = true;
	}

	public void UpdatePushToken(final String pushToken) {
		this.taskExecuter.execute(new Runnable() {
			public void run() {
				BytePushHTTPManager.this.SendPushTokenUpdate(pushToken);
				BytePushHTTPManager.this.taskExecuter.shutdown();
			}
		});
	}

	public void UpdatePushStats(final Intent data) {
		this.taskExecuter.execute(new Runnable() {
			public void run() {
				BytePushHTTPManager.this.SendPushNotificationUpdates(data);
				BytePushHTTPManager.this.taskExecuter.shutdown();
			}
		});
	}

	public void UpdateUninstallStats(final Intent data) {
		this.taskExecuter.execute(new Runnable() {
			public void run() {
				BytePushHTTPManager.this.SendUninstallNotificationUpdates(data);
				BytePushHTTPManager.this.taskExecuter.shutdown();
			}
		});
	}

	public void GetPushConfigs(final PushTaskExecuted taskExecuted) {
		this.taskExecuter.execute(new Runnable() {
			public void run() {
				String dataSTR = BytePushHTTPManager.this.GetPushNotificationSettings();
				taskExecuted.onTaskDone(dataSTR);
				BytePushHTTPManager.this.taskExecuter.shutdown();
			}
		});
	}

	private String SendPushTokenUpdate(String pushToken) {
		try {
			Log.d("ByteBrew Push", "Sending Push Request");
			URL _apiURL = new URL("https://push-api.bytebrew.io/push/services/token");
			HttpURLConnection _apiConnection = (HttpURLConnection) _apiURL.openConnection();
			_apiConnection.setRequestMethod("POST");
			_apiConnection.setRequestProperty("sdk-key", this.sdkKey);
			_apiConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
			_apiConnection.setRequestProperty("Accept", "application/json");
			_apiConnection.setDoOutput(true);
			_apiConnection.setDoInput(true);
			_apiConnection.connect();
			JSONObject data = new JSONObject();
			data.put("user", this.userID);
			data.put("token", pushToken);
			data.put("gameID", this.gameID);
			OutputStream outputStream = _apiConnection.getOutputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
			writer.write(data.toString());
			writer.close();
			outputStream.close();
			Log.d("ByteBrew HTTP", "Sent Push Request Connect");
			if (_apiConnection.getResponseCode() == 200)
				return "Success";
			return "Failed";
		} catch (Exception e) {
			e.printStackTrace();
			return "Failed";
		}
	}

	private String SendPushNotificationUpdates(Intent data) {
		try {
			Log.d("ByteBrew Push", "Sending Push Stats");
			URL _apiURL = new URL("https://push-api.bytebrew.io/push/services/logs");
			HttpURLConnection _apiConnection = (HttpURLConnection) _apiURL.openConnection();
			_apiConnection.setRequestMethod("POST");
			_apiConnection.setRequestProperty("sdk-key", this.sdkKey);
			_apiConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
			_apiConnection.setRequestProperty("Accept", "application/json");
			_apiConnection.setDoOutput(true);
			_apiConnection.setDoInput(true);
			JSONObject postData = new JSONObject();
			postData.put("user_id", this.userID);
			postData.put("log_type", data.getStringExtra("pushIntent"));
			postData.put("game_id", this.gameID);
			postData.put("sent_id", data.getStringExtra("byte_message_sent_id"));
			if (data.getStringExtra("pushIntent").equals("OPENED"))
				postData.put("session_id", data.getStringExtra("session_id"));
			OutputStream outputStream = _apiConnection.getOutputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
			writer.write(postData.toString());
			writer.close();
			outputStream.close();
			Log.d("ByteBrew HTTP", "Sent Push Request Stats");
			if (_apiConnection.getResponseCode() == 200)
				return "Success";
			return "Failed";
		} catch (Exception e) {
			Log.d("ByteBrew HTTP", "Push Stat Update Failed " + e.getMessage());
			return "Failed";
		}
	}

	private String SendUninstallNotificationUpdates(Intent data) {
		try {
			Log.d("ByteBrew Push", "Sending");
			URL _apiURL = new URL("https://push-api.bytebrew.io/push/services/uninstall");
			HttpURLConnection _apiConnection = (HttpURLConnection) _apiURL.openConnection();
			_apiConnection.setRequestMethod("POST");
			_apiConnection.setRequestProperty("sdk-key", this.sdkKey);
			_apiConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
			_apiConnection.setRequestProperty("Accept", "application/json");
			_apiConnection.setDoOutput(true);
			_apiConnection.setDoInput(true);
			JSONObject postData = new JSONObject();
			postData.put("user_id", this.userID);
			postData.put("log_type", "uninstall");
			postData.put("game_id", this.gameID);
			OutputStream outputStream = _apiConnection.getOutputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
			writer.write(postData.toString());
			writer.close();
			outputStream.close();
			Log.d("ByteBrew HTTP", "Sent Request");
			if (_apiConnection.getResponseCode() == 200)
				return "Success";
			return "Failed";
		} catch (Exception e) {
			Log.d("ByteBrew HTTP", "Update Failed " + e.getMessage());
			return "Failed";
		}
	}

	private String GetPushNotificationSettings() {
		try {
			Log.d("ByteBrew Push", "Retrieving Push Settings");
			URL _apiURL = new URL("https://push-api.bytebrew.io/push/services/config");
			HttpURLConnection _apiConnection = (HttpURLConnection) _apiURL.openConnection();
			_apiConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
			_apiConnection.setRequestProperty("Accept", "application/json");
			_apiConnection.setRequestProperty("sdk-key", this.sdkKey);
			_apiConnection.setRequestMethod("GET");
			_apiConnection.connect();
			if (_apiConnection.getResponseCode() == 200) {
				InputStream it = new BufferedInputStream(_apiConnection.getInputStream());
				InputStreamReader read = new InputStreamReader(it);
				BufferedReader buff = new BufferedReader(read);
				StringBuilder dta = new StringBuilder();
				String chunks;
				while ((chunks = buff.readLine()) != null)
					dta.append(chunks);
				Log.d("ByteBrew Push", "Successfully retrieved Push Configs");
				return dta.toString();
			}
			Log.d("ByteBrew Push Error", "Did not successfully retrieve Push Configs");
			return "";
		} catch (Exception e) {
			e.printStackTrace();
			Log.d("ByteBrew Push Error", "Getting configs " + e.getMessage());
			return "";
		}
	}
}
