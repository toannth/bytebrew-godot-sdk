//
// © 2026-present https://github.com/ByteBrewIO
//

package org.godotengine.plugin.bytebrew;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.godotengine.godot.Godot;
import org.godotengine.godot.Dictionary;
import org.godotengine.godot.plugin.GodotPlugin;
import org.godotengine.godot.plugin.SignalInfo;
import org.godotengine.godot.plugin.UsedByGodot;

import com.bytebrew.bytebrewlibrary.ByteBrew;
import com.bytebrew.bytebrewlibrary.ByteBrewAdType;
import com.bytebrew.bytebrewlibrary.ByteBrewProgressionType;
import com.bytebrew.bytebrewlibrary.ByteBrewPurchaseResult;
import com.bytebrew.bytebrewlibrary.PurchaseResponseListener;
import com.bytebrew.bytebrewlibrary.RemoteConfigListener;


public class ByteBrewPlugin extends GodotPlugin {
	public static final String PLUGIN_NAME = "ByteBrew";
	static final String LOG_TAG = "godot::" + PLUGIN_NAME;


	static final String REMOTE_CONFIGS_UPDATED_SIGNAL = "remote_configs_updated";
	static final String IAP_PURCHASE_RESULT_CALLBACK_SIGNAL = "iap_purchase_result_callback";

	private Activity activity;

	public ByteBrewPlugin(Godot godot) {
		super(godot);
	}

	@UsedByGodot
	public void InitializeByteBrew(String appID, String appKey, String engineVersion, String buildVersion) {
		ByteBrew.InitializeByteBrew(appID, appKey, engineVersion, buildVersion, activity.getApplicationContext());
	}

	@UsedByGodot
	public void StartPushNotifications() {
		ByteBrew.StartPushNotifications(activity.getApplicationContext());
	}

	@UsedByGodot
	public void SetCustomDataWithStringValue(String key, String value) {
		ByteBrew.SetCustomData(key, value);
	}

	@UsedByGodot
	public void SetCustomDataWithDoubleValue(String key, double value) {
		ByteBrew.SetCustomData(key, value);
	}

	@UsedByGodot
	public void SetCustomDataWithIntegerValue(String key, int value) {
		ByteBrew.SetCustomData(key, value);
	}

	@UsedByGodot
	public void SetCustomDataWithBooleanValue(String key, boolean value) {
		ByteBrew.SetCustomData(key, value);
	}

	@UsedByGodot
	public void NewCustomEvent(String eventName) {
		ByteBrew.NewCustomEvent(eventName);
	}

	@UsedByGodot
	public void NewCustomEventWithStringValue(String eventName, String value) {
		ByteBrew.NewCustomEvent(eventName, value);
	}

	@UsedByGodot
	public void NewCustomEventWithFloatValue(String eventName, float value) {
		ByteBrew.NewCustomEvent(eventName, value);
	}

	@UsedByGodot
	public void NewProgressionEvent(int progressionType, String environment, String stage) {
		ByteBrew.NewProgressionEvent(GetProgressionType(progressionType), environment, stage);
	}

	@UsedByGodot
	public void NewProgressionEventWithStringValue(int progressionType, String environment, String stage, String value) {
		ByteBrew.NewProgressionEvent(GetProgressionType(progressionType), environment, stage, value);
	}

	@UsedByGodot
	public void NewProgressionEventWithFloatValue(int progressionType, String environment, String stage, float value) {
		ByteBrew.NewProgressionEvent(GetProgressionType(progressionType), environment, stage, value);
	}

	private static ByteBrewProgressionType GetProgressionType(int progressionType) {
		switch (progressionType) {
			case 0:
				return ByteBrewProgressionType.Started;
			case 1:
				return ByteBrewProgressionType.Completed;
			default:
				return ByteBrewProgressionType.Failed;
		}
	}

	@UsedByGodot
	public void TrackAdEvent(int adType, String adProvider, String adUnitName, double revenue) {
		ByteBrew.TrackAdEvent(GetAdType(adType), adProvider, adUnitName, revenue);
	}

	@UsedByGodot
	public void TrackAdEventWithAdLocation(int adType, String adProvider, String adUnitName, String adLocation, double revenue) {
		ByteBrew.TrackAdEvent(GetAdType(adType), adProvider, adUnitName, adLocation, revenue);
	}

	private static ByteBrewAdType GetAdType(int adType) {
		switch (adType) {
			case 0:
				return ByteBrewAdType.Interstitial;
			case 1:
				return ByteBrewAdType.Reward;
			default:
				return ByteBrewAdType.Banner;
		}
	}

	@UsedByGodot
	public void TrackInAppPurchaseEvent(String store, String currency, float amount, String itemID, String category) {
		ByteBrew.TrackInAppPurchaseEvent(store, currency, amount, itemID, category);
	}

	@UsedByGodot
	public void TrackGoogleInAppPurchaseEvent(String store, String currency, float amount, String itemID, String category, String receipt, String signature) {
		ByteBrew.TrackGoogleInAppPurchaseEvent(store, currency, amount, itemID, category, receipt, signature);
	}

	@UsedByGodot
	public void ValidateGoogleInAppPurchaseEvent(String store, String currency, float amount, String itemID, String category, String receipt, String signature) {
		ByteBrew.ValidateGoogleInAppPurchaseEvent(store, currency, amount, itemID, category, receipt, signature, new PurchaseResponseListener() {
			public void purchaseValidated(ByteBrewPurchaseResult purchaseResult) {
				emitSignal(SIGNAL_NAME_REVIEW_INFO_GENERATED);
			}
		});
	}

	@UsedByGodot
	public void LoadRemoteConfigs() {
		ByteBrew.LoadRemoteConfigs(new RemoteConfigListener() {
			public void RetrievedConfigs(boolean status) {
				emitSignal(REMOTE_CONFIGS_UPDATED_SIGNAL, status);
			}
		});
	}

	@UsedByGodot
	public boolean HasRemoteConfigsBeenSet() {
		return ByteBrew.HasRemoteConfigsBeenSet();
	}

	@UsedByGodot
	public String RetrieveRemoteConfigValue(String key, String defaultValue) {
		return ByteBrew.RetrieveRemoteConfigValue(key, defaultValue);
	}

	@UsedByGodot
	public void RestartTracking() {
		ByteBrew.RestartTracking(activity.getApplicationContext());
	}

	@UsedByGodot
	public void StopTracking() {
		ByteBrew.StopTracking(activity.getApplicationContext());
	}

	@UsedByGodot
	public String GetUserID() {
		return ByteBrew.GetUserID();
	}

	@Override
	public String getPluginName() {
		return PLUGIN_NAME;
	}

	@Override
	public Set<SignalInfo> getPluginSignals() {
		Set<SignalInfo> signals = new HashSet<>();
		signals.add(new SignalInfo(REMOTE_CONFIGS_UPDATED_SIGNAL, Boolean.class));
		signals.add(new SignalInfo(IAP_PURCHASE_RESULT_CALLBACK_SIGNAL, Dictionary.class));

		return signals;
	}

	@Override
	public View onMainCreate(Activity activity) {
		this.activity = activity;
		return super.onMainCreate(activity);
	}

	@Override
	public void onMainDestroy() {
		super.onMainDestroy();
	}
}
