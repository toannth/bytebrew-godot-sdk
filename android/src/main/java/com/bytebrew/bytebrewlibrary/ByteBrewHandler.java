package com.bytebrew.bytebrewlibrary;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.json.JSONException;
import org.json.JSONObject;

class ByteBrewHandler extends Activity {
	public static ByteBrewHandler _instance = null;

	private static Context currentContext = null;

	private static String androidDeviceID;

	private static String androidBuildID;

	private static String engineVersion;

	private static String buildVersion;

	private static String googleADID;

	private static String GEOCode;

	private static String packageName;

	private static String deviceModel;

	private static String deviceMaker;

	private static String deviceCarrier;

	private static String deviceCarrierCodes;

	private static String screenSize;

	private static String osVersion;

	private static String deviceName;

	private static String phoneCapacity;

	private static final String PLATFORM = "Android";

	protected static String gameID;

	protected static String DEVKEY;

	protected static String userID;

	protected static String sessionID;

	protected static String sessionKey;

	private static boolean trackingEnabled;

	private static String language;

	private static String deviceTimeZone;

	private static Date _startTime;

	private static JSONObject remoteConfigurations;

	private static boolean isCurrentUser;

	private static String bb_sdk_version = "0.1.6";

	private static ByteBrewUtils byteBrewUtils;

	public ByteBrewHandler() {
		this.initializationCalled = false;
		byteBrewUtils = new ByteBrewUtils();
	}

	private static final ExecutorService executorService = Executors.newCachedThreadPool();

	protected boolean initializationCalled;

	public static ByteBrewHandler getInstance() {
		if (_instance == null)
			_instance = new ByteBrewHandler();
		return _instance;
	}

	public static Context getCurrentContext() {
		return currentContext;
	}

	public void InitializeByteBrew(String gameId, String gameKey, String engineV, String buildV, Context context) {
		Log.i("ByteBrew Message", "Starting Initialization");
		if (gameId.length() == 0)
			throw new IllegalArgumentException("App ID must contain a string");
		if (gameKey.length() == 0)
			throw new IllegalArgumentException("App Key must contain a string");
		if (engineV.length() == 0)
			throw new IllegalArgumentException("Engine Version must contain a string");
		if (context == null)
			throw new IllegalArgumentException("Context cannot be null");
		currentContext = context;
		isCurrentUser = byteBrewUtils.isCurrentUser(context);
		trackingEnabled = byteBrewUtils.isTrackingEnabled(currentContext);
		if (!trackingEnabled)
			return;
		engineVersion = engineV;
		buildVersion = buildV;
		androidDeviceID = byteBrewUtils.GetAndroidDeviceID(context);
		androidBuildID = byteBrewUtils.GetDeviceBuildID();
		GEOCode = byteBrewUtils.getCountryCode(context).toUpperCase();
		deviceName = byteBrewUtils.GetDeviceName(context);
		deviceMaker = byteBrewUtils.GetDeviceMaker();
		screenSize = byteBrewUtils.GeScreenSize(context);
		phoneCapacity = byteBrewUtils.GetStorageCapacity();
		deviceCarrier = byteBrewUtils.GetDeviceCarrier(context);
		deviceCarrierCodes = byteBrewUtils.GetDeviceCarrierCodes(context);
		gameID = gameId;
		DEVKEY = gameKey;
		packageName = context.getPackageName();
		language = byteBrewUtils.GetLanguageSettings();
		deviceTimeZone = byteBrewUtils.GetDeviceTimeZone();
		osVersion = Build.VERSION.RELEASE;
		deviceModel = byteBrewUtils.GetDeviceModel();
		_startTime = new Date();
		sessionID = byteBrewUtils.CreateSessionID();
		sessionKey = "";
		if (isCurrentUser) {
			userID = byteBrewUtils.GetUserID(context);
			CreateUserEvent();
		} else {
			userID = byteBrewUtils.SetUserID(context);
			CreateNewUser();
		}
		byteBrewUtils.SetAppConfig(gameId, gameKey, currentContext);
		this.initializationCalled = true;
	}

	public void SetAdID(String adID) {
		googleADID = adID;
	}

	private void CreateNewUser() {
		if (!trackingEnabled)
			return;
		final JSONObject logEvent = new JSONObject();
		try {
			JSONObject externalData = new JSONObject();
			externalData.put("eventType", "new_user");
			externalData.put("deviceID", androidDeviceID);
			externalData.put("buildID", androidBuildID);
			externalData.put("userLocale", language);
			externalData.put("userTimeZone", deviceTimeZone);
			logEvent.put("user_adid", googleADID);
			logEvent.put("game_id", gameID);
			logEvent.put("sdk_version", bb_sdk_version);
			logEvent.put("geo", GEOCode);
			logEvent.put("engine_version", engineVersion);
			logEvent.put("version_number", buildVersion);
			logEvent.put("bundle_id", packageName);
			logEvent.put("deviceCarrier", deviceCarrier);
			logEvent.put("carrierCodes", deviceCarrierCodes);
			logEvent.put("deviceScreenSize", screenSize);
			logEvent.put("deviceCapacity", phoneCapacity);
			logEvent.put("device_maker", deviceMaker);
			logEvent.put("device_name", deviceName);
			logEvent.put("device", deviceModel);
			logEvent.put("os_version", osVersion);
			logEvent.put("platform", "Android");
			logEvent.put("category", "user");
			logEvent.put("user_id", userID);
			logEvent.put("session_id", sessionID);
			logEvent.put("externalData", externalData);
		} catch (JSONException exception) {
			Log.i("ByteBrew Exception", exception.getMessage());
			return;
		}
		try {
			Log.i("ByteBrew", "Creating New User Event");
			final ByteBrewReferrerRetriever referrerRetriever = new ByteBrewReferrerRetriever(currentContext);
			referrerRetriever.RetrieveReferrer(new ByteBrewReferrerStatusListener() {
				public void OnFinished() {
					if (logEvent.optJSONObject("externalData") != null && referrerRetriever.GetRefererPackage() != null)
						try {
							logEvent.getJSONObject("externalData").put("referrerPayload",
									referrerRetriever.GetRefererPackage().toString());
						} catch (Exception jsonException) {
							Log.i("ByteBrew Exception", jsonException.getMessage());
						}
					(new ByteBrewInitializer(ByteBrewHandler.executorService, ByteBrewHandler.gameID,
							ByteBrewHandler.DEVKEY)).InitializeUser(logEvent, ByteBrewHandler.currentContext);
				}
			});
		} catch (Exception e) {
			Log.i("ByteBrew Exception", "Create New User: " + e.getMessage());
		}
	}

	protected static void UserResumed() {
		if (!trackingEnabled)
			return;
		_startTime = new Date();
		JSONObject logEvent = new JSONObject();
		try {
			JSONObject externalData = new JSONObject();
			externalData.put("eventType", "game_open");
			externalData.put("userLocale", language);
			externalData.put("userTimeZone", deviceTimeZone);
			externalData.put("buildID", androidBuildID);
			logEvent.put("user_adid", googleADID);
			logEvent.put("game_id", gameID);
			logEvent.put("sdk_version", bb_sdk_version);
			logEvent.put("geo", GEOCode);
			logEvent.put("engine_version", engineVersion);
			logEvent.put("version_number", buildVersion);
			logEvent.put("bundle_id", packageName);
			logEvent.put("deviceCarrier", deviceCarrier);
			logEvent.put("carrierCodes", deviceCarrierCodes);
			logEvent.put("deviceScreenSize", screenSize);
			logEvent.put("deviceCapacity", phoneCapacity);
			logEvent.put("device_maker", deviceMaker);
			logEvent.put("device_name", deviceName);
			logEvent.put("device", deviceModel);
			logEvent.put("os_version", osVersion);
			logEvent.put("platform", "Android");
			logEvent.put("category", "user");
			logEvent.put("user_id", userID);
			logEvent.put("session_id", sessionID);
			logEvent.put("externalData", externalData);
		} catch (JSONException exception) {
			Log.i("ByteBrew Exception", exception.getMessage());
			return;
		}
		try {
			Log.i("ByteBrew", "Creating User Event");
			(new ByteBrewInitializer(executorService, gameID, DEVKEY)).InitializeUser(logEvent, currentContext);
		} catch (Exception e) {
			Log.i("ByteBrew Exception", "Create User Resume: " + e.getMessage());
		}
	}

	public void LoadRemoteConfigurations(final ByteBrewRemoteConfigResponse retrieval) {
		try {
			Log.i("ByteBrew", "Getting remote configurations.");
			(new ByteBrewHTTPManager(executorService, gameID, DEVKEY)).GetData(new ByteBrewHTTPRetrievalResponse() {
				public void retrievedConfigs(boolean status) {
					Log.i("ByteBrew", "Remote Configs Responded");
					retrieval.loadedConfigs(status);
				}
			});
		} catch (Exception e) {
			Log.i("ByteBrew Exception", "LoadRemoteConfigurations: " + e.getMessage());
		}
	}

	private void CreateUserEvent() {
		if (!trackingEnabled)
			return;
		JSONObject logEvent = new JSONObject();
		try {
			JSONObject externalData = new JSONObject();
			externalData.put("eventType", "game_open");
			externalData.put("userLocale", language);
			externalData.put("userTimeZone", deviceTimeZone);
			externalData.put("buildID", androidBuildID);
			logEvent.put("user_adid", googleADID);
			logEvent.put("game_id", gameID);
			logEvent.put("sdk_version", bb_sdk_version);
			logEvent.put("geo", GEOCode);
			logEvent.put("engine_version", engineVersion);
			logEvent.put("version_number", buildVersion);
			logEvent.put("bundle_id", packageName);
			logEvent.put("deviceCarrier", deviceCarrier);
			logEvent.put("carrierCodes", deviceCarrierCodes);
			logEvent.put("deviceScreenSize", screenSize);
			logEvent.put("deviceCapacity", phoneCapacity);
			logEvent.put("device_maker", deviceMaker);
			logEvent.put("device_name", deviceName);
			logEvent.put("device", deviceModel);
			logEvent.put("os_version", osVersion);
			logEvent.put("platform", "Android");
			logEvent.put("category", "user");
			logEvent.put("user_id", userID);
			logEvent.put("session_id", sessionID);
			logEvent.put("externalData", externalData);
		} catch (JSONException exception) {
			Log.i("ByteBrew Exception", exception.getMessage());
			return;
		}
		try {
			Log.i("ByteBrew", "Creating User Event");
			(new ByteBrewInitializer(executorService, gameID, DEVKEY)).InitializeUser(logEvent, currentContext);
		} catch (Exception e) {
			Log.i("ByteBrew Exception", "Create User Event: " + e.getMessage());
		}
	}

	public static void CreateSessionEndEvent() {
		if (!trackingEnabled)
			return;
		Date endTime = new Date();
		int secondsPlayed = (int) ((endTime.getTime() - _startTime.getTime()) / 1000L);
		JSONObject logEvent = new JSONObject();
		try {
			JSONObject externalData = new JSONObject();
			externalData.put("sessionLength", "" + secondsPlayed);
			logEvent.put("user_adid", googleADID);
			logEvent.put("game_id", gameID);
			logEvent.put("sdk_version", bb_sdk_version);
			logEvent.put("geo", GEOCode);
			logEvent.put("engine_version", engineVersion);
			logEvent.put("version_number", buildVersion);
			logEvent.put("bundle_id", packageName);
			logEvent.put("deviceCarrier", deviceCarrier);
			logEvent.put("carrierCodes", deviceCarrierCodes);
			logEvent.put("deviceScreenSize", screenSize);
			logEvent.put("deviceCapacity", phoneCapacity);
			logEvent.put("device_maker", deviceMaker);
			logEvent.put("device_name", deviceName);
			logEvent.put("device", deviceModel);
			logEvent.put("os_version", osVersion);
			logEvent.put("platform", "Android");
			logEvent.put("category", "game_close");
			logEvent.put("user_id", userID);
			logEvent.put("session_id", sessionID);
			logEvent.put("session_key", sessionKey);
			logEvent.put("externalData", externalData);
		} catch (JSONException exception) {
			Log.i("ByteBrew Exception", exception.getMessage());
			return;
		}
		try {
			Log.i("ByteBrew: ", "Creating Session End Event");
			(new ByteBrewHTTPManager(executorService, gameID, DEVKEY)).Send(logEvent);
		} catch (Exception e) {
			Log.i("ByteBrew Exception", "CreateNewUser: " + e.getMessage());
		}
	}

	public void SendCustomDataAttribution(String items) {
		if (!trackingEnabled)
			return;
		JSONObject logEvent = null;
		try {
			logEvent = new JSONObject(items);
			logEvent.put("game_id", gameID);
			logEvent.put("bundle_id", packageName);
			logEvent.put("platform", "Android");
			logEvent.put("user_id", userID);
		} catch (JSONException exception) {
			Log.i("ByteBrew Exception", exception.getMessage());
			return;
		}
		try {
			Log.i("ByteBrew: ", "Creating Custom Data update");
			(new ByteBrewHTTPManager(executorService, gameID, DEVKEY)).Send(logEvent, true);
		} catch (Exception e) {
			Log.i("ByteBrew Exception", "CustomDataUpdate: " + e.getMessage());
		}
	}

	public void CreateCustomEvent(String items) {
		if (!trackingEnabled)
			return;
		JSONObject logEvent = null;
		try {
			logEvent = new JSONObject(items);
			logEvent.getJSONObject("externalData").put("buildID", androidBuildID);
			logEvent.getJSONObject("externalData").put("userLocale", language);
			logEvent.getJSONObject("externalData").put("userTimeZone", deviceTimeZone);
			logEvent.put("user_adid", googleADID);
			logEvent.put("game_id", gameID);
			logEvent.put("sdk_version", bb_sdk_version);
			logEvent.put("geo", GEOCode);
			logEvent.put("engine_version", engineVersion);
			logEvent.put("version_number", buildVersion);
			logEvent.put("bundle_id", packageName);
			logEvent.put("deviceCarrier", deviceCarrier);
			logEvent.put("carrierCodes", deviceCarrierCodes);
			logEvent.put("deviceScreenSize", screenSize);
			logEvent.put("deviceCapacity", phoneCapacity);
			logEvent.put("device_maker", deviceMaker);
			logEvent.put("device_name", deviceName);
			logEvent.put("device", deviceModel);
			logEvent.put("os_version", osVersion);
			logEvent.put("platform", "Android");
			logEvent.put("user_id", userID);
			logEvent.put("session_id", sessionID);
			logEvent.put("session_key", sessionKey);
		} catch (JSONException exception) {
			Log.i("ByteBrew Exception", exception.getMessage());
			return;
		}
		try {
			Log.i("ByteBrew: ", "Creating Custom Event");
			(new ByteBrewHTTPManager(executorService, gameID, DEVKEY)).Send(logEvent);
		} catch (Exception e) {
			Log.i("ByteBrew Exception", "CreateCustomEvent: " + e.getMessage());
		}
	}

	public void ValidateIAPEvent(String items, final ByteBrewPurchaseValidationResult resultCallback) {
		if (!trackingEnabled)
			return;
		JSONObject logEvent = null;
		try {
			logEvent = new JSONObject(items);
			logEvent.getJSONObject("externalData").put("buildID", androidBuildID);
			logEvent.getJSONObject("externalData").put("userLocale", language);
			logEvent.getJSONObject("externalData").put("userTimeZone", deviceTimeZone);
			logEvent.put("user_adid", googleADID);
			logEvent.put("game_id", gameID);
			logEvent.put("sdk_version", bb_sdk_version);
			logEvent.put("geo", GEOCode);
			logEvent.put("engine_version", engineVersion);
			logEvent.put("version_number", buildVersion);
			logEvent.put("bundle_id", packageName);
			logEvent.put("deviceCarrier", deviceCarrier);
			logEvent.put("carrierCodes", deviceCarrierCodes);
			logEvent.put("deviceScreenSize", screenSize);
			logEvent.put("deviceCapacity", phoneCapacity);
			logEvent.put("device_maker", deviceMaker);
			logEvent.put("device_name", deviceName);
			logEvent.put("device", deviceModel);
			logEvent.put("os_version", osVersion);
			logEvent.put("platform", "Android");
			logEvent.put("user_id", userID);
			logEvent.put("session_id", sessionID);
			logEvent.put("session_key", sessionKey);
		} catch (JSONException exception) {
			Log.i("ByteBrew Exception", exception.getMessage());
			return;
		}
		try {
			Log.i("ByteBrew: ", "Testing validation of purchase");
			(new ByteBrewHTTPManager(executorService, gameID, DEVKEY)).ValidateData(logEvent,
					new ByteBrewPurchaseValidationResult() {
						public void purchaseValidated(ByteBrewPurchaseResult result) {
							resultCallback.purchaseValidated(result);
						}
					});
		} catch (Exception e) {
			Log.i("ByteBrew Exception", "ValidateIAPEvent: " + e.getMessage());
		}
	}

	public JSONObject GetAdSessionRequestBody() {
		JSONObject adSessionReqBody = new JSONObject();
		try {
			adSessionReqBody.put("user_adid", googleADID);
			adSessionReqBody.put("user_id", userID);
			adSessionReqBody.put("game_id", gameID);
			adSessionReqBody.put("geo", GEOCode);
			adSessionReqBody.put("engine_version", engineVersion);
			adSessionReqBody.put("version_number", buildVersion);
			adSessionReqBody.put("bundle_id", packageName);
			adSessionReqBody.put("deviceCarrier", deviceCarrier);
			adSessionReqBody.put("carrierCodes", deviceCarrierCodes);
			adSessionReqBody.put("deviceScreenSize", screenSize);
			adSessionReqBody.put("deviceCapacity", phoneCapacity);
			adSessionReqBody.put("device_maker", deviceMaker);
			adSessionReqBody.put("device_name", deviceName);
			adSessionReqBody.put("device", deviceModel);
			adSessionReqBody.put("os_version", osVersion);
			adSessionReqBody.put("platform", "Android");
			adSessionReqBody.put("session_id", sessionID);
			adSessionReqBody.put("session_key", sessionKey);
			adSessionReqBody.put("sdk_version", bb_sdk_version);
			adSessionReqBody.put("time_zone", deviceTimeZone);
			adSessionReqBody.put("locale", language);
			adSessionReqBody.put("device_buildID", androidBuildID);
		} catch (JSONException exception) {
			Log.i("ByteBrew Exception", (exception.getMessage() != null) ? exception.getMessage() : "");
			return null;
		}
		return adSessionReqBody;
	}

	public JSONObject GetCrossPromoAdRequestBody(String adUnitID, String adUnitType, String adOrientationType,
			boolean ctrlOnly) {
		JSONObject adReqBody = new JSONObject();
		try {
			adReqBody.put("ad_unitid", adUnitID);
			adReqBody.put("ad_unit_type", adUnitType);
			adReqBody.put("ad_orientation_type", adOrientationType);
			adReqBody.put("ctrl_only", ctrlOnly);
			adReqBody.put("user_adid", googleADID);
			adReqBody.put("game_id", gameID);
			adReqBody.put("geo", GEOCode);
			adReqBody.put("engine_version", engineVersion);
			adReqBody.put("version_number", buildVersion);
			adReqBody.put("bundle_id", packageName);
			adReqBody.put("deviceCarrier", deviceCarrier);
			adReqBody.put("carrierCodes", deviceCarrierCodes);
			adReqBody.put("deviceScreenSize", screenSize);
			adReqBody.put("deviceCapacity", phoneCapacity);
			adReqBody.put("device_maker", deviceMaker);
			adReqBody.put("device_name", deviceName);
			adReqBody.put("device", deviceModel);
			adReqBody.put("os_version", osVersion);
			adReqBody.put("platform", "Android");
			adReqBody.put("user_id", userID);
			adReqBody.put("session_id", sessionID);
			adReqBody.put("session_key", sessionKey);
			adReqBody.put("sdk_version", bb_sdk_version);
			adReqBody.put("time_zone", deviceTimeZone);
			adReqBody.put("locale", language);
			adReqBody.put("device_buildID", androidBuildID);
		} catch (JSONException exception) {
			Log.i("ByteBrew Exception", (exception.getMessage() != null) ? exception.getMessage() : "");
			return null;
		}
		return adReqBody;
	}

	public JSONObject GetAdEventRequestBody(String adRequestID, String adUnitID, String adUnitType,
			String adOrientationType, String adID, String adActionType) {
		JSONObject adReqBody = new JSONObject();
		try {
			adReqBody.put("user_adid", googleADID);
			adReqBody.put("user_id", userID);
			adReqBody.put("game_id", gameID);
			adReqBody.put("geo", GEOCode);
			adReqBody.put("engine_version", engineVersion);
			adReqBody.put("version_number", buildVersion);
			adReqBody.put("bundle_id", packageName);
			adReqBody.put("deviceCarrier", deviceCarrier);
			adReqBody.put("carrierCodes", deviceCarrierCodes);
			adReqBody.put("deviceScreenSize", screenSize);
			adReqBody.put("deviceCapacity", phoneCapacity);
			adReqBody.put("device_maker", deviceMaker);
			adReqBody.put("device_name", deviceName);
			adReqBody.put("device", deviceModel);
			adReqBody.put("os_version", osVersion);
			adReqBody.put("platform", "Android");
			adReqBody.put("session_id", sessionID);
			adReqBody.put("session_key", sessionKey);
			adReqBody.put("sdk_version", bb_sdk_version);
			adReqBody.put("time_zone", deviceTimeZone);
			adReqBody.put("locale", language);
			adReqBody.put("device_buildID", androidBuildID);
			adReqBody.put("ad_unit_id", adUnitID);
			adReqBody.put("ad_unit_type", adUnitType);
			adReqBody.put("ad_unit_orientation", adOrientationType);
			adReqBody.put("adrequest_id", adRequestID);
			adReqBody.put("campaign_name", "");
			adReqBody.put("campaign_id", "");
			adReqBody.put("creative_name", "");
			adReqBody.put("creative_id", "");
			adReqBody.put("ad_id", adID);
			adReqBody.put("ad_actiontype", adActionType);
		} catch (JSONException exception) {
			Log.i("ByteBrew Exception", (exception.getMessage() != null) ? exception.getMessage() : "");
			return null;
		}
		return adReqBody;
	}

	public static void SetSessionKey(String key) {
		sessionKey = key;
	}

	public static void SetRemoteConfigurations(JSONObject configs) {
		remoteConfigurations = configs;
	}

	public boolean IsRemoteConfigsSet() {
		return (remoteConfigurations != null);
	}

	public String GetRemoteConfigForKey(String key, String defaultValue) {
		if (remoteConfigurations == null)
			return defaultValue;
		try {
			return remoteConfigurations.getString(key);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public void StartTracking(Context context) {
		if (context == null) {
			trackingEnabled = true;
			byteBrewUtils.StartTracking(currentContext);
		} else {
			trackingEnabled = true;
			byteBrewUtils.StartTracking(context);
		}
	}

	public void StopTracking(Context context) {
		if (context == null) {
			trackingEnabled = false;
			byteBrewUtils.StopTracking(currentContext);
		} else {
			trackingEnabled = false;
			byteBrewUtils.StopTracking(context);
		}
	}
}
