package com.bytebrew.bytebrewlibrary;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import java.text.DecimalFormat;
import org.json.JSONException;
import org.json.JSONObject;

public class ByteBrew {
	private static boolean isInitialized = false;

	protected static void SetSDKInitialized() {
		isInitialized = true;
		ByteBrewPushNotifications.ByteBrewSDKInitialized();
	}

	public static void InitializeByteBrew(String appID, String appKey, String engineVersion, String buildVersion,
			Context context) {
		if (isInitialized)
			return;
		ByteBrewListener.CreateListeners((Application) context.getApplicationContext());
		ByteBrewHandler.getInstance().InitializeByteBrew(appID, appKey, engineVersion, buildVersion, context);
	}

	public static boolean IsByteBrewInitialized() {
		return isInitialized;
	}

	public static void StartPushNotifications(Context context) {
		if (!(ByteBrewHandler.getInstance()).initializationCalled)
			Log.d("ByteBrew Message", "You must call the ByteBrew Initialization first.");
		ByteBrewPushNotifications.StartByteBrewPushNotifications(context);
	}

	public static void SetCustomData(String key, String value) {
		if (!isInitialized)
			return;
		JSONObject event = new JSONObject();
		try {
			event.put("key", key);
			event.put("value", value);
			event.put("type", "string");
			ByteBrewHandler.getInstance().SendCustomDataAttribution(event.toString());
		} catch (JSONException exception) {
			Log.i("ByteBrew Exception", exception.getMessage());
		}
	}

	public static void SetCustomData(String key, double value) {
		if (!isInitialized)
			return;
		JSONObject event = new JSONObject();
		try {
			event.put("key", key);
			event.put("value", Double.toString(value));
			event.put("type", "double");
			ByteBrewHandler.getInstance().SendCustomDataAttribution(event.toString());
		} catch (JSONException exception) {
			Log.i("ByteBrew Exception", exception.getMessage());
		}
	}

	public static void SetCustomData(String key, int value) {
		if (!isInitialized)
			return;
		JSONObject event = new JSONObject();
		try {
			event.put("key", key);
			event.put("value", Integer.toString(value));
			event.put("type", "integer");
			ByteBrewHandler.getInstance().SendCustomDataAttribution(event.toString());
		} catch (JSONException exception) {
			Log.i("ByteBrew Exception", exception.getMessage());
		}
	}

	public static void SetCustomData(String key, boolean value) {
		if (!isInitialized)
			return;
		JSONObject event = new JSONObject();
		try {
			event.put("key", key);
			event.put("value", Boolean.toString(value));
			event.put("type", "boolean");
			ByteBrewHandler.getInstance().SendCustomDataAttribution(event.toString());
		} catch (JSONException exception) {
			Log.i("ByteBrew Exception", exception.getMessage());
		}
	}

	public static void NewCustomEvent(String eventName) {
		if (!isInitialized)
			return;
		JSONObject event = new JSONObject();
		try {
			event.put("category", "custom");
			JSONObject externalData = new JSONObject();
			externalData.put("eventType", eventName);
			event.put("externalData", externalData);
			ByteBrewHandler.getInstance().CreateCustomEvent(event.toString());
		} catch (JSONException exception) {
			Log.i("ByteBrew Exception", exception.getMessage());
		}
	}

	public static void NewCustomEvent(String eventName, String value) {
		if (!isInitialized)
			return;
		JSONObject event = new JSONObject();
		try {
			JSONObject externalData = new JSONObject();
			externalData.put("eventType", eventName);
			externalData.put("value", value);
			event.put("category", "custom");
			event.put("externalData", externalData);
			ByteBrewHandler.getInstance().CreateCustomEvent(event.toString());
		} catch (JSONException exception) {
			Log.i("ByteBrew Exception", exception.getMessage());
		}
	}

	public static void NewCustomEvent(String eventName, float value) {
		if (!isInitialized)
			return;
		JSONObject event = new JSONObject();
		try {
			JSONObject externalData = new JSONObject();
			externalData.put("eventType", eventName);
			externalData.put("value", Float.toString(value));
			event.put("category", "custom");
			event.put("externalData", externalData);
			ByteBrewHandler.getInstance().CreateCustomEvent(event.toString());
		} catch (JSONException exception) {
			Log.i("ByteBrew Exception", exception.getMessage());
		}
	}

	public static void NewProgressionEvent(ByteBrewProgressionType progressionStatus, String environment,
			String stage) {
		if (!isInitialized)
			return;
		JSONObject event = new JSONObject();
		try {
			JSONObject externalData = new JSONObject();
			externalData.put("progressionStatus", progressionStatus.GetName());
			externalData.put("progressionEnvironment", environment);
			externalData.put("progressionStage", stage);
			event.put("category", "progression");
			event.put("externalData", externalData);
			ByteBrewHandler.getInstance().CreateCustomEvent(event.toString());
		} catch (JSONException exception) {
			Log.i("ByteBrew Exception", exception.getMessage());
		}
	}

	public static void NewProgressionEvent(ByteBrewProgressionType progressionStatus, String environment, String stage,
			String value) {
		if (!isInitialized)
			return;
		JSONObject event = new JSONObject();
		try {
			JSONObject externalData = new JSONObject();
			externalData.put("progressionStatus", progressionStatus.GetName());
			externalData.put("progressionEnvironment", environment);
			externalData.put("progressionStage", stage);
			externalData.put("progressionValue", value);
			event.put("category", "progression");
			event.put("externalData", externalData);
			ByteBrewHandler.getInstance().CreateCustomEvent(event.toString());
		} catch (JSONException exception) {
			Log.i("ByteBrew Exception", exception.getMessage());
		}
	}

	public static void NewProgressionEvent(ByteBrewProgressionType progressionStatus, String environment, String stage,
			float value) {
		if (!isInitialized)
			return;
		JSONObject event = new JSONObject();
		try {
			JSONObject externalData = new JSONObject();
			externalData.put("progressionStatus", progressionStatus.GetName());
			externalData.put("progressionEnvironment", environment);
			externalData.put("progressionStage", stage);
			externalData.put("progressionValue", Float.toString(value));
			event.put("category", "progression");
			event.put("externalData", externalData);
			ByteBrewHandler.getInstance().CreateCustomEvent(event.toString());
		} catch (JSONException exception) {
			Log.i("ByteBrew Exception", exception.getMessage());
		}
	}

	public static void TrackAdEvent(ByteBrewAdType adType, String adLocation) {
		if (!isInitialized)
			return;
		JSONObject event = new JSONObject();
		try {
			JSONObject externalData = new JSONObject();
			externalData.put("eventType", "adEvent");
			externalData.put("placementType", adType.GetName());
			externalData.put("adLocation", adLocation);
			event.put("category", "custom");
			event.put("externalData", externalData);
			ByteBrewHandler.getInstance().CreateCustomEvent(event.toString());
		} catch (JSONException exception) {
			Log.i("ByteBrew Exception", exception.getMessage());
		}
	}

	public static void TrackAdEvent(ByteBrewAdType adType, String adLocation, String AdID) {
		if (!isInitialized)
			return;
		JSONObject event = new JSONObject();
		try {
			JSONObject externalData = new JSONObject();
			externalData.put("eventType", "adEvent");
			externalData.put("placementType", adType.GetName());
			externalData.put("adLocation", adLocation);
			externalData.put("ADID", AdID);
			event.put("category", "custom");
			event.put("externalData", externalData);
			ByteBrewHandler.getInstance().CreateCustomEvent(event.toString());
		} catch (JSONException exception) {
			Log.i("ByteBrew Exception", exception.getMessage());
		}
	}

	public static void TrackAdEvent(ByteBrewAdType adType, String adLocation, String AdID, String adProvider) {
		if (!isInitialized)
			return;
		JSONObject event = new JSONObject();
		try {
			JSONObject externalData = new JSONObject();
			externalData.put("eventType", "adEvent");
			externalData.put("placementType", adType.GetName());
			externalData.put("adLocation", adLocation);
			externalData.put("ADID", AdID);
			externalData.put("adProvider", adProvider);
			event.put("category", "custom");
			event.put("externalData", externalData);
			ByteBrewHandler.getInstance().CreateCustomEvent(event.toString());
		} catch (JSONException exception) {
			Log.i("ByteBrew Exception", exception.getMessage());
		}
	}

	public static void TrackAdEvent(ByteBrewAdType adType, String adProvider, String adUnitName, double revenue) {
		if (!isInitialized)
			return;
		JSONObject event = new JSONObject();
		try {
			JSONObject externalData = new JSONObject();
			externalData.put("eventType", "adEvent");
			externalData.put("placementType", adType.GetName());
			externalData.put("adProvider", adProvider);
			externalData.put("adUnitName", adUnitName);
			DecimalFormat df = new DecimalFormat("#");
			df.setMaximumFractionDigits(20);
			externalData.put("revenue", df.format(revenue));
			event.put("category", "custom");
			event.put("externalData", externalData);
			ByteBrewHandler.getInstance().CreateCustomEvent(event.toString());
		} catch (JSONException exception) {
			Log.i("ByteBrew Exception", exception.getMessage());
		}
	}

	public static void TrackAdEvent(ByteBrewAdType adType, String adProvider, String adUnitName, String adLocation,
			double revenue) {
		if (!isInitialized)
			return;
		JSONObject event = new JSONObject();
		try {
			JSONObject externalData = new JSONObject();
			externalData.put("eventType", "adEvent");
			externalData.put("placementType", adType.GetName());
			externalData.put("adProvider", adProvider);
			externalData.put("adUnitName", adUnitName);
			externalData.put("adLocation", adLocation);
			DecimalFormat df = new DecimalFormat("#");
			df.setMaximumFractionDigits(20);
			externalData.put("revenue", df.format(revenue));
			event.put("category", "custom");
			event.put("externalData", externalData);
			ByteBrewHandler.getInstance().CreateCustomEvent(event.toString());
		} catch (JSONException exception) {
			Log.i("ByteBrew Exception", exception.getMessage());
		}
	}

	@Deprecated
	public static void TrackAdEvent(String placementType, String adLocation) {
		if (!isInitialized)
			return;
		JSONObject event = new JSONObject();
		try {
			JSONObject externalData = new JSONObject();
			externalData.put("eventType", "adEvent");
			externalData.put("placementType", placementType);
			externalData.put("adLocation", adLocation);
			event.put("category", "custom");
			event.put("externalData", externalData);
			ByteBrewHandler.getInstance().CreateCustomEvent(event.toString());
		} catch (JSONException exception) {
			Log.i("ByteBrew Exception", exception.getMessage());
		}
	}

	@Deprecated
	public static void TrackAdEvent(String placementType, String adLocation, String AdID) {
		if (!isInitialized)
			return;
		JSONObject event = new JSONObject();
		try {
			JSONObject externalData = new JSONObject();
			externalData.put("eventType", "adEvent");
			externalData.put("placementType", placementType);
			externalData.put("adLocation", adLocation);
			externalData.put("ADID", AdID);
			event.put("category", "custom");
			event.put("externalData", externalData);
			ByteBrewHandler.getInstance().CreateCustomEvent(event.toString());
		} catch (JSONException exception) {
			Log.i("ByteBrew Exception", exception.getMessage());
		}
	}

	@Deprecated
	public static void TrackAdEvent(String placementType, String adLocation, String AdID, String adProvider) {
		if (!isInitialized)
			return;
		JSONObject event = new JSONObject();
		try {
			JSONObject externalData = new JSONObject();
			externalData.put("eventType", "adEvent");
			externalData.put("placementType", placementType);
			externalData.put("adLocation", adLocation);
			externalData.put("ADID", AdID);
			externalData.put("adProvider", adProvider);
			event.put("category", "custom");
			event.put("externalData", externalData);
			ByteBrewHandler.getInstance().CreateCustomEvent(event.toString());
		} catch (JSONException exception) {
			Log.i("ByteBrew Exception", exception.getMessage());
		}
	}

	public static void TrackInAppPurchaseEvent(String store, String currency, float amount, String itemID,
			String category) {
		if (!isInitialized)
			return;
		JSONObject event = new JSONObject();
		try {
			JSONObject externalData = new JSONObject();
			externalData.put("eventType", "IAPEvent");
			externalData.put("store", store);
			externalData.put("currency", currency);
			externalData.put("amount", Float.toString(amount));
			externalData.put("itemID", itemID);
			externalData.put("category", category);
			event.put("category", "custom");
			event.put("externalData", externalData);
			ByteBrewHandler.getInstance().CreateCustomEvent(event.toString());
		} catch (JSONException exception) {
			Log.i("ByteBrew Exception", exception.getMessage());
		}
	}

	public static void TrackGoogleInAppPurchaseEvent(String store, String currency, float amount, String itemID,
			String category, String receipt, String signature) {
		if (!isInitialized)
			return;
		JSONObject event = new JSONObject();
		try {
			JSONObject externalData = new JSONObject();
			externalData.put("eventType", "IAPEvent");
			externalData.put("store", store);
			externalData.put("currency", currency);
			externalData.put("amount", Float.toString(amount));
			externalData.put("itemID", itemID);
			externalData.put("category", category);
			externalData.put("receipt", receipt);
			externalData.put("signature", signature);
			event.put("category", "custom");
			event.put("externalData", externalData);
			ByteBrewHandler.getInstance().CreateCustomEvent(event.toString());
		} catch (JSONException exception) {
			Log.i("ByteBrew Exception", exception.getMessage());
		}
	}

	public static void ValidateGoogleInAppPurchaseEvent(String store, String currency, float amount, String itemID,
			String category, String receipt, String signature, final PurchaseResponseListener listener) {
		if (!isInitialized)
			return;
		JSONObject event = new JSONObject();
		try {
			JSONObject externalData = new JSONObject();
			externalData.put("eventType", "IAPEvent");
			externalData.put("store", store);
			externalData.put("currency", currency);
			externalData.put("amount", Float.toString(amount));
			externalData.put("itemID", itemID);
			externalData.put("category", category);
			externalData.put("receipt", receipt);
			externalData.put("signature", signature);
			event.put("category", "custom");
			event.put("externalData", externalData);
			ByteBrewHandler.getInstance().ValidateIAPEvent(event.toString(), new ByteBrewPurchaseValidationResult() {
				public void purchaseValidated(ByteBrewPurchaseResult result) {
					listener.purchaseValidated(result);
				}
			});
		} catch (JSONException exception) {
			Log.i("ByteBrew Exception", exception.getMessage());
		}
	}

	public static void LoadRemoteConfigs(final RemoteConfigListener configListener) {
		ByteBrewHandler.getInstance().LoadRemoteConfigurations(new ByteBrewRemoteConfigResponse() {
			public void loadedConfigs(boolean status) {
				configListener.RetrievedConfigs(status);
			}
		});
	}

	public static boolean HasRemoteConfigsBeenSet() {
		return ByteBrewHandler.getInstance().IsRemoteConfigsSet();
	}

	public static String RetrieveRemoteConfigValue(String key, String defaultValue) {
		return ByteBrewHandler.getInstance().GetRemoteConfigForKey(key, defaultValue);
	}

	public static void RestartTracking(Context context) {
		ByteBrewHandler.getInstance().StartTracking(context);
	}

	public static void StopTracking(Context context) {
		ByteBrewHandler.getInstance().StopTracking(context);
	}

	public static String GetUserID() {
		if (!isInitialized)
			return null;
		return ByteBrewHandler.userID;
	}
}
