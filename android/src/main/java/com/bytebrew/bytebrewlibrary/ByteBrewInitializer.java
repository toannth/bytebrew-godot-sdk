package com.bytebrew.bytebrewlibrary;

import android.content.Context;
import android.util.Log;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import org.json.JSONObject;

class ByteBrewInitializer {
	protected String key;

	protected String gameID = null;

	protected static final String _baseurl = "https://api.bytebrew.io/api/game/logs/add";

	public ByteBrewHTTPRetrievalResponse loadDelegate;

	private Executor executor;

	public ByteBrewInitializer(Executor currentExecutor, String gameID, String key) {
		this.executor = currentExecutor;
		this.gameID = gameID;
		this.key = key;
	}

	public void InitializeUser(final JSONObject data, final Context context) {
		this.executor.execute(new Runnable() {
			public void run() {
				if (data == null)
					return;
				try {
					if (data.has("user_adid")) {
						ByteBrewInitializer.this.InitializeUserPrivate(data, context);
					} else {
						String advertID = ByteBrewInitializer.this.GetUserAdvertisingID(context);
						data.put("user_adid", advertID);
						ByteBrewInitializer.this.InitializeUserPrivate(data, context);
					}
				} catch (Exception e) {
					Log.i("ByteBrew Exception", "Couldn't Initialize: " + e.getMessage());
				}
			}
		});
	}

	private String GetUserAdvertisingID(Context context) {
		try {
			if (!AdvertisingIdClient.getAdvertisingIdInfo(context).isLimitAdTrackingEnabled()) {
				AdvertisingIdClient.Info idInfo = null;
				try {
					idInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
				} catch (GooglePlayServicesNotAvailableException e) {
					e.printStackTrace();
				} catch (GooglePlayServicesRepairableException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				String advertId = null;
				try {
					advertId = idInfo.getId();
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				ByteBrewHandler.getInstance().SetAdID(advertId);
				return advertId;
			}
			ByteBrewHandler.getInstance().SetAdID("");
			return "";
		} catch (Exception e) {
			Log.i("ByteBrew Exception", "Couldn't Retrieve Advertising ID: " + e.getMessage());
			return "";
		}
	}

	private void InitializeUserPrivate(JSONObject data, Context context) {
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
				Log.i("ByteBrew: ", "ByteBrew Initialized");
				ByteBrew.SetSDKInitialized();
			} else {
				Log.i("ByteBrew Exception",
						": " + urlConnection.getResponseMessage() + " : " + urlConnection.getResponseCode());
			}
		} catch (Exception e) {
			Log.i("ByteBrew Exception", "Couldn't Initialize: " + e.getMessage());
		}
	}
}
