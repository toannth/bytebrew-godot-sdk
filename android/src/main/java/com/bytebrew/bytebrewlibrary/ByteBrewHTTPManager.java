package com.bytebrew.bytebrewlibrary;

import android.util.Log;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.Executor;
import org.json.JSONObject;

class ByteBrewHTTPManager {
	protected String key;

	protected String gameID = null;

	protected static final String _baseurl = "https://api.bytebrew.io/api/game/logs/add";

	protected static final String _purchaseValidationurl = "https://api.bytebrew.io/api/game/purchase/validate";

	protected static final String _usercustomdataurl = "https://api.bytebrew.io/api/game/userdata/update";

	protected static final String _remoteconfigurl = "https://api.bytebrew.io/api/game/configurations/remote/";

	private Executor executor;

	public ByteBrewHTTPManager(Executor currentExecutor, String gameID, String key) {
		this.executor = currentExecutor;
		this.gameID = gameID;
		this.key = key;
	}

	public void Send(final JSONObject data) {
		this.executor.execute(new Runnable() {
			public void run() {
				ByteBrewHTTPManager.this.SendEventData(data);
			}
		});
	}

	public void Send(final JSONObject data, boolean isCustomData) {
		this.executor.execute(new Runnable() {
			public void run() {
				ByteBrewHTTPManager.this.SendCustomUserData(data);
			}
		});
	}

	public void GetData(final ByteBrewHTTPRetrievalResponse response) {
		this.executor.execute(new Runnable() {
			public void run() {
				boolean configRequest = ByteBrewHTTPManager.this.SendRemoteConfigRequest();
				Log.i("ByteBrew", "Remote retrieved");
				response.retrievedConfigs(configRequest);
			}
		});
	}

	public void ValidateData(final JSONObject data, final ByteBrewPurchaseValidationResult validationResult) {
		this.executor.execute(new Runnable() {
			public void run() {
				ByteBrewPurchaseResult result = ByteBrewHTTPManager.this.ValidatePurchaseData(data);
				validationResult.purchaseValidated(result);
			}
		});
	}

	private void SendEventData(JSONObject data) {
		try {
			URL url = new URL("https://api.bytebrew.io/api/game/logs/add");
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("POST");
			urlConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
			urlConnection.setRequestProperty("Accept", "application/json");
			urlConnection.setRequestProperty("sdk-key", this.key);
			urlConnection.setDoOutput(true);
			urlConnection.setDoInput(true);
			urlConnection.connect();
			OutputStream outputStream = urlConnection.getOutputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
			writer.write(data.toString());
			writer.close();
			outputStream.close();
			int statusCode = urlConnection.getResponseCode();
			if (statusCode == 200) {
				if (urlConnection.getHeaderField("session_key") != null)
					ByteBrewHandler.SetSessionKey(urlConnection.getHeaderField("session_key"));
				Log.i("ByteBrew", "Event Sent");
			} else {
				Log.i("ByteBrew Exception",
						": " + urlConnection.getResponseMessage() + " : " + urlConnection.getResponseCode());
			}
		} catch (Exception e) {
			Log.i("ByteBrew Exception", "Couldn't send Event: " + e.getMessage());
		}
	}

	private void SendCustomUserData(JSONObject data) {
		try {
			URL url = new URL("https://api.bytebrew.io/api/game/userdata/update");
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("POST");
			urlConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
			urlConnection.setRequestProperty("Accept", "application/json");
			urlConnection.setRequestProperty("sdk-key", this.key);
			urlConnection.setDoOutput(true);
			urlConnection.setDoInput(true);
			OutputStream outputStream = urlConnection.getOutputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
			writer.write(data.toString());
			writer.close();
			outputStream.close();
			int statusCode = urlConnection.getResponseCode();
			if (statusCode == 200) {
				if (urlConnection.getHeaderField("session_key") != null)
					ByteBrewHandler.SetSessionKey(urlConnection.getHeaderField("session_key"));
				Log.i("ByteBrew", "Custom Data Attributed");
			} else {
				Log.i("ByteBrew Exception",
						": " + urlConnection.getResponseMessage() + " : " + urlConnection.getResponseCode());
			}
		} catch (Exception e) {
			Log.i("ByteBrew Exception", "Couldn't send data: " + e.getMessage());
		}
	}

	private boolean SendRemoteConfigRequest() {
		try {
			URL url = new URL("https://api.bytebrew.io/api/game/configurations/remote/" + this.gameID);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
			urlConnection.setRequestProperty("Accept", "application/json");
			urlConnection.setRequestProperty("sdk-key", this.key);
			urlConnection.setRequestProperty("user_id", ByteBrewHandler.userID);
			int statusCode = urlConnection.getResponseCode();
			if (statusCode == 200) {
				urlConnection.getResponseMessage();
				BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
				StringBuffer response = new StringBuffer();
				String inputSTR;
				while ((inputSTR = in.readLine()) != null)
					response.append(inputSTR);
				in.close();
				JSONObject remoteConfigs = new JSONObject(response.toString());
				ByteBrewHandler.SetRemoteConfigurations(remoteConfigs);
				Log.i("ByteBrew", "Remote configurations retrieved");
				return true;
			}
			Log.i("ByteBrew Exception",
					": " + urlConnection.getResponseMessage() + " : " + urlConnection.getResponseCode());
			ByteBrewHandler.SetRemoteConfigurations(new JSONObject());
			return false;
		} catch (Exception e) {
			Log.i("ByteBrew Exception", "Couldn't retrieve configurations: " + e.getMessage());
			ByteBrewHandler.SetRemoteConfigurations(new JSONObject());
			return false;
		}
	}

	private ByteBrewPurchaseResult ValidatePurchaseData(JSONObject data) {
		try {
			URL url = new URL("https://api.bytebrew.io/api/game/purchase/validate");
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("POST");
			urlConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
			urlConnection.setRequestProperty("Accept", "application/json");
			urlConnection.setRequestProperty("sdk-key", this.key);
			urlConnection.setDoOutput(true);
			urlConnection.setDoInput(true);
			urlConnection.connect();
			OutputStream outputStream = urlConnection.getOutputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
			writer.write(data.toString());
			writer.close();
			outputStream.close();
			int statusCode = urlConnection.getResponseCode();
			if (statusCode == 200) {
				if (urlConnection.getHeaderField("session_key") != null)
					ByteBrewHandler.SetSessionKey(urlConnection.getHeaderField("session_key"));
				urlConnection.getResponseMessage();
				BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
				StringBuffer response = new StringBuffer();
				String inputSTR;
				while ((inputSTR = in.readLine()) != null)
					response.append(inputSTR);
				in.close();
				JSONObject purchaseResultData = new JSONObject(response.toString());
				Log.i("ByteBrew", "Purchase Validation Received");
				return new ByteBrewPurchaseResult(purchaseResultData.getBoolean("isValid"), true,
						purchaseResultData.getString("message"), purchaseResultData.getString("itemID"),
						purchaseResultData.getString("timestamp"));
			}
			Log.i("ByteBrew API Error",
					": " + urlConnection.getResponseMessage() + " : " + urlConnection.getResponseCode());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			return new ByteBrewPurchaseResult(false, false, "Error occurred trying to validate, check game configs.",
					data.getJSONObject("externalData").getString("itemID"), sdf.format(new Date()));
		} catch (Exception e) {
			Log.i("ByteBrew Exception", "Couldn't validate purchase: " + e.getMessage());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			return new ByteBrewPurchaseResult(false, false, "Error occurred trying to validate, check game configs.",
					"", sdf.format(new Date()));
		}
	}
}
