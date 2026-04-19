package com.bytebrew.bytebrewlibrary;

import android.content.Context;
import android.util.Log;
import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONObject;

public class ByteBrewReferrerRetriever {
	private InstallReferrerClient referrerClient;

	private Timer retrievalTimer;

	private ByteBrewReferrerStatusListener callbackListener;

	private JSONObject referrerPackage = null;

	private final int RETRIEVE_TIMEOUT = 300;

	private boolean calledRetriever = false;

	private boolean callFinished = false;

	public ByteBrewReferrerRetriever(Context context) {
		CreateReferrerClient(context);
	}

	private void CreateReferrerClient(Context context) {
		try {
			this.referrerClient = InstallReferrerClient.newBuilder(context).build();
		} catch (Exception exception) {
			Log.d("ByteBrew", "Error creating a InstallReferrerClient: " + exception.getMessage());
		}
	}

	public void RetrieveReferrer(ByteBrewReferrerStatusListener referrerStatusListener) {
		if (this.calledRetriever)
			return;
		this.callbackListener = referrerStatusListener;
		if (this.referrerClient == null &&
				this.callbackListener != null && !this.callFinished) {
			this.callbackListener.OnFinished();
			this.callFinished = true;
		}
		try {
			long startTime = System.currentTimeMillis();
			this.calledRetriever = true;
			this.referrerClient.startConnection(new InstallReferrerStateListener() {
				public void onInstallReferrerSetupFinished(int i) {
					Log.d("ByteBrew", "Referrer Connection Finished");
					switch (i) {
						case 0:
							try {
								ReferrerDetails response = ByteBrewReferrerRetriever.this.referrerClient
										.getInstallReferrer();
								String referrerUrl = response.getInstallReferrer();
								long referrerClickTime = response.getReferrerClickTimestampSeconds();
								long appInstallTime = response.getInstallBeginTimestampSeconds();
								boolean instantExperienceLaunched = response.getGooglePlayInstantParam();
								Log.d("ByteBrew", "Referrer received success response.");
								ByteBrewReferrerRetriever.this.referrerPackage = new JSONObject();
								ByteBrewReferrerRetriever.this.referrerPackage.put("referrerURL", referrerUrl);
								ByteBrewReferrerRetriever.this.referrerPackage.put("referrerClickTime",
										referrerClickTime);
								ByteBrewReferrerRetriever.this.referrerPackage.put("appInstallTime", appInstallTime);
								ByteBrewReferrerRetriever.this.referrerPackage.put("instantExperienceLaunched",
										instantExperienceLaunched);
							} catch (Exception e) {
								e.printStackTrace();
							}
							break;
						case 2:
							Log.d("ByteBrew", "Referrer feature not supported..");
							break;
						case 1:
							Log.d("ByteBrew", "Referrer failed to establish connection");
							break;
					}
					ByteBrewReferrerRetriever.this.ClearTimer();
					if (ByteBrewReferrerRetriever.this.callbackListener != null
							&& !ByteBrewReferrerRetriever.this.callFinished) {
						ByteBrewReferrerRetriever.this.callbackListener.OnFinished();
						ByteBrewReferrerRetriever.this.callFinished = true;
					}
				}

				public void onInstallReferrerServiceDisconnected() {
					Log.d("ByteBrew", "Referrer failed to establish connection");
					ByteBrewReferrerRetriever.this.ClearTimer();
					if (ByteBrewReferrerRetriever.this.callbackListener != null
							&& !ByteBrewReferrerRetriever.this.callFinished) {
						ByteBrewReferrerRetriever.this.callbackListener.OnFinished();
						ByteBrewReferrerRetriever.this.callFinished = true;
					}
				}
			});
			this.retrievalTimer = new Timer();
			this.retrievalTimer.schedule(new TimerTask() {
				public void run() {
					ByteBrewReferrerRetriever.this.TimerFinished();
				}
			}, 300L);
		} catch (Exception exception) {
			Log.d("ByteBrew", "There was an error retrieving: " + exception.getMessage());
			ClearTimer();
			if (this.referrerClient != null)
				this.referrerClient.endConnection();
			if (this.callbackListener != null && !this.callFinished) {
				this.callbackListener.OnFinished();
				this.callFinished = true;
			}
		}
	}

	public JSONObject GetRefererPackage() {
		return this.referrerPackage;
	}

	private void TimerFinished() {
		Log.d("ByteBrew", "Referrer timed out, proceeding.");
		if (this.referrerClient != null && !this.callFinished) {
			this.referrerClient.endConnection();
			this.callbackListener.OnFinished();
			this.callFinished = true;
			this.callbackListener = null;
		}
	}

	private void ClearTimer() {
		if (this.retrievalTimer != null)
			this.retrievalTimer.cancel();
	}
}
