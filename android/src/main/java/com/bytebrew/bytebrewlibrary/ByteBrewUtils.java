package com.bytebrew.bytebrewlibrary;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import java.io.IOException;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;
import org.json.JSONObject;

class ByteBrewUtils {
	private static Context currentcontext;

	public boolean isTrackingEnabled(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences("bytebrew_prefs", 0);
		boolean trackingDisabled = sharedPreferences.getBoolean("trackingDisabled", false);
		if (!trackingDisabled)
			return true;
		return false;
	}

	public void StopTracking(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences("bytebrew_prefs", 0);
		boolean trackingDisabled = sharedPreferences.getBoolean("trackingDisabled", false);
		if (!trackingDisabled) {
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putBoolean("trackingDisabled", true);
			editor.apply();
		}
	}

	public void StartTracking(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences("bytebrew_prefs", 0);
		boolean trackingDisabled = sharedPreferences.getBoolean("trackingDisabled", false);
		if (trackingDisabled) {
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putBoolean("trackingDisabled", false);
			editor.apply();
		}
	}

	public String GetAppVersion(Context context) {
		try {
			PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			String version = pInfo.versionName;
			return version;
		} catch (android.content.pm.PackageManager.NameNotFoundException e) {
			e.printStackTrace();
			return "1.0.0";
		}
	}

	public boolean isCurrentUser(Context context) {
		String installTime = "";
		SharedPreferences sharedPreferences = context.getSharedPreferences("bytebrew_prefs", 0);
		installTime = sharedPreferences.getString("installTime", "");
		try {
			long firstInstallTime = (context.getPackageManager().getPackageInfo(context.getPackageName(),
					0)).firstInstallTime;
			long lastUpdateTime = (context.getPackageManager().getPackageInfo(context.getPackageName(),
					0)).lastUpdateTime;
			String firstValue = String.valueOf(firstInstallTime);
			String lastValue = String.valueOf(lastUpdateTime);
			if (installTime == null || installTime.isEmpty()) {
				if (firstInstallTime == lastUpdateTime) {
					Log.d("ByteBrew", "Fresh new install.");
				} else {
					Log.d("ByteBrew", "App updated with ByteBrew in it.");
				}
				return false;
			}
			if (!installTime.equals(firstValue)) {
				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.clear();
				editor.apply();
				return false;
			}
			return true;
		} catch (android.content.pm.PackageManager.NameNotFoundException e) {
			e.printStackTrace();
			return true;
		}
	}

	public void SetAppConfig(String gameID, String SDKKey, Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences("bytebrew_prefs", 0);
		try {
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString("game_id", gameID);
			editor.putString("key", SDKKey);
			editor.apply();
		} catch (Exception e) {
			Log.d("ByteBrew Error", "Setting App Config: " + e.getMessage());
		}
	}

	public JSONObject GetAppConfig(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences("bytebrew_prefs", 0);
		String gameID = sharedPreferences.getString("game_id", "");
		String SDKKey = sharedPreferences.getString("key", "");
		String userID = sharedPreferences.getString("user_id", "");
		JSONObject appConfig = new JSONObject();
		try {
			if (gameID != null || !gameID.isEmpty())
				appConfig.put("game_id", gameID);
			if (SDKKey != null || !SDKKey.isEmpty())
				appConfig.put("key", SDKKey);
			if (userID != null || !userID.isEmpty())
				appConfig.put("user_id", userID);
		} catch (Exception e) {
			Log.d("ByteBrew Error", "Getting App Config: " + e.getMessage());
		}
		return appConfig;
	}

	public String SetUserID(Context context) {
		String userID = "";
		SharedPreferences sharedPreferences = context.getSharedPreferences("bytebrew_prefs", 0);
		userID = sharedPreferences.getString("user_id", "");
		try {
			long firstInstallTime = (context.getPackageManager().getPackageInfo(context.getPackageName(),
					0)).firstInstallTime;
			String firstValue = String.valueOf(firstInstallTime);
			userID = UUID.randomUUID().toString();
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString("user_id", userID);
			editor.putString("installTime", firstValue);
			editor.apply();
			return userID;
		} catch (android.content.pm.PackageManager.NameNotFoundException e) {
			e.printStackTrace();
			return userID;
		}
	}

	public String CreateSessionID() {
		return UUID.randomUUID().toString();
	}

	public String GetUserID(Context context) {
		String userID = "";
		SharedPreferences sharedPreferences = context.getSharedPreferences("bytebrew_prefs", 0);
		userID = sharedPreferences.getString("user_id", "");
		if (userID == null || userID.isEmpty()) {
			userID = UUID.randomUUID().toString();
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString("user_id", userID);
			editor.apply();
		}
		return userID;
	}

	public String getCountryCode(Context context) {
		try {
			TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
			if (telephonyManager != null) {
				String simCC = telephonyManager.getSimCountryIso();
				if (simCC != null && simCC.length() == 2)
					return simCC;
				if (telephonyManager.getPhoneType() != 2) {
					String networkCC = telephonyManager.getNetworkCountryIso();
					if (networkCC != null && networkCC.length() == 2)
						return networkCC;
				} else {
					return (context.getResources().getConfiguration()).locale.getCountry();
				}
			} else {
				return (context.getResources().getConfiguration()).locale.getCountry();
			}
		} catch (Exception e) {
			Log.d("ByteBrew Exception", e.getMessage());
			return "";
		}
		return "";
	}

	public String GeScreenSize(Context context) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		int width = metrics.widthPixels;
		int height = metrics.heightPixels;
		String sizes = width + "x" + height;
		return sizes;
	}

	public String GetDeviceCarrier(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
		String simOperatorName = telephonyManager.getSimOperatorName();
		String networkOperatorName = telephonyManager.getNetworkOperatorName();
		if (!simOperatorName.isEmpty())
			return simOperatorName;
		if (!networkOperatorName.isEmpty())
			return networkOperatorName;
		return "";
	}

	public String GetDeviceCarrierCodes(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
		String simStuff = telephonyManager.getSimOperator();
		String networkStuff = telephonyManager.getNetworkOperator();
		if (!simStuff.isEmpty())
			return simStuff.substring(0, 3) + "-" + simStuff.substring(3);
		if (!networkStuff.isEmpty())
			return networkStuff.substring(0, 3) + "-" + networkStuff.substring(3);
		return "";
	}

	public String GetDeviceMaker() {
		return Build.MANUFACTURER;
	}

	public String GetStorageCapacity() {
		String datadirect = Environment.getDataDirectory().getAbsolutePath();
		long totalBytes = (new StatFs(datadirect)).getTotalBytes();
		return (totalBytes / 1073741824L) + "GB";
	}

	public String GetDeviceName(Context context) {
		String deviceName = "";
		try {
			context.getContentResolver();
			deviceName = Settings.Secure.getString(context.getContentResolver(), "bluetooth_name");
		} catch (Exception e) {
			Log.d("ByteBrew Exception", ": " + e.getMessage());
		}
		return deviceName;
	}

	public String GetDeviceModel() {
		String manufacturer = Build.MANUFACTURER;
		String model = Build.MODEL;
		if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
			String tempModel = model.substring(0, manufacturer.length());
			tempModel = tempModel.trim();
			return tempModel;
		}
		return model;
	}

	public String GetDeviceBuildID() {
		try {
			return Build.ID;
		} catch (Exception exception) {
			Log.d("ByteBrew Error", exception.getMessage());
			return "";
		}
	}

	private String capitalize(String s) {
		if (s == null || s.length() == 0)
			return "";
		char first = s.charAt(0);
		if (Character.isUpperCase(first))
			return s;
		return Character.toUpperCase(first) + s.substring(1);
	}

	public static class GetGoogleAdvertisingInfo extends AsyncTask<Void, Void, String> {
		protected String doInBackground(Void... params) {
			AdvertisingIdClient.Info idInfo = null;
			try {
				idInfo = AdvertisingIdClient.getAdvertisingIdInfo(ByteBrewUtils.currentcontext);
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
			return advertId;
		}
	}

	public String GetAdvertisingID(Context context) {
		currentcontext = context;
		try {
			if (!AdvertisingIdClient.getAdvertisingIdInfo(currentcontext).isLimitAdTrackingEnabled()) {
				String advertisingID = AdvertisingIdClient.getAdvertisingIdInfo(currentcontext).getId();
				String str1 = (String) (new GetGoogleAdvertisingInfo()).execute().get();
				if (!advertisingID.isEmpty() || advertisingID != null)
					return advertisingID;
				if (!str1.isEmpty() || str1 != null)
					return str1;
				return "";
			}
			String googleADID = (String) (new GetGoogleAdvertisingInfo()).execute().get();
			if (!googleADID.isEmpty() || googleADID != null)
				return googleADID;
			return "";
		} catch (Exception e) {
			Log.d("ByteBrew Exception", ": " + e.getMessage());
			return null;
		}
	}

	public String GetAndroidDeviceID(Context context) {
		try {
			return Settings.Secure.getString(context.getContentResolver(), "android_id");
		} catch (Exception exception) {
			Log.d("ByteBrew Error", exception.getMessage());
			return "";
		}
	}

	public String GetLanguageSettings() {
		return Locale.getDefault().getLanguage();
	}

	public String GetDeviceTimeZone() {
		return TimeZone.getDefault().getID();
	}
}
