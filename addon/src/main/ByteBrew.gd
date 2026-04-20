#
# © 2026-present https://github.com/ByteBrewIO
#

extends Node

signal remote_config_load_complete(status: bool)
signal in_app_purchase_validated(purchase_result: ByteBrewPurchaseResult)

const PLUGIN_SINGLETON_NAME: String = "@pluginName@"

var _plugin_singleton: Object
var _platform := OS.get_name()


func _ready() -> void:
	_update_plugin()


func _notification(what: int) -> void:
	if what == NOTIFICATION_APPLICATION_RESUMED:
		_update_plugin()


func _update_plugin() -> void:
	if _plugin_singleton == null:
		if Engine.has_singleton(PLUGIN_SINGLETON_NAME):
			_plugin_singleton = Engine.get_singleton(PLUGIN_SINGLETON_NAME)
			_connect_signals()


func _connect_signals() -> void:
	_plugin_singleton.remote_config_load_complete.connect(_on_remote_config_load_complete)
	_plugin_singleton.in_app_purchase_validated.connect(_on_in_app_purchase_validated)


func initialize(game_id: String, game_key: String, version: String) -> void:
	if _plugin_singleton:
		_plugin_singleton.InitializeByteBrew(game_id, game_key, Engine.get_version_info(), version)


func is_initialized() -> bool:
	if _plugin_singleton:
		return _plugin_singleton.IsByteBrewInitialized()
	return false


func set_custom_data(key: String, value: Variant) -> void:
	if _plugin_singleton:
		match typeof(value):
			TYPE_BOOL:
				_plugin_singleton.SetCustomDataWithBooleanValue(key, value)
			TYPE_INT:
				_plugin_singleton.SetCustomDataWithIntegerValue(key, value)
			TYPE_FLOAT:
				_plugin_singleton.SetCustomDataWithDoubleValue(key, value)
			_:
				_plugin_singleton.SetCustomDataWithStringValue(key, str(value))


func new_custom_event(event_name: String, value: Variant = null) -> void:
	if _plugin_singleton:
		if value != null:
			match typeof(value):
				TYPE_FLOAT:
					_plugin_singleton.NewCustomEventWithFloatValue(event_name, value)
				_:
					_plugin_singleton.NewCustomEventWithStringValue(event_name, str(value))
		else:
			_plugin_singleton.NewCustomEvent(event_name)


func new_progression_event(
	status: ByteBrewProgressionType.Value,
	environment: String,
	stage: String,
	value: Variant = null
) -> void:
	if _plugin_singleton:
		if value != null:
			match typeof(value):
				TYPE_FLOAT:
					_plugin_singleton.NewProgressionEventWithFloatValue(
						status,
						environment,
						stage,
						value
					)
				_:
					_plugin_singleton.NewProgressionEventWithStringValue(
						status,
						environment,
						stage,
						str(value)
					)
		else:
			_plugin_singleton.NewProgressionEvent(status, environment, stage)


func track_ad_event(ad_type: ByteBrewAdType.Value, ad_provider: String, ad_id: String, revenue: float, ad_location := "")-> void:
	if _plugin_singleton:
		if ad_location:
			_plugin_singleton.TrackAdEventWithAdLocationRevenue(
				ad_type,
				ad_provider,
				ad_id,
				ad_location,
				revenue
			)
		else:
			_plugin_singleton.TrackAdEventWithRevenue(ad_type, ad_provider, ad_id, revenue)


func track_in_app_purchase_event(
	store: String,
	currency: String,
	amount: float,
	item_id: String,
	category: String
) -> void:
	if _plugin_singleton:
		_plugin_singleton.TrackInAppPurchaseEvent(store, currency, amount, item_id, category)


func track_in_app_purchase_event_with_receipt(
	store: String,
	currency: String,
	amount: float,
	item_id: String,
	category: String,
	receipt: String,
	signature: String = ""
) -> void:
	if _plugin_singleton:
		if _platform == "Android":
			_plugin_singleton.TrackGoogleInAppPurchaseEvent(
				store,
				currency,
				amount,
				item_id,
				category,
				receipt,
				signature
			)
		elif _platform == "iOS":
			_plugin_singleton.TrackiOSInAppPurchaseEvent(
				store,
				currency,
				amount,
				item_id,
				category,
				receipt
			)


func validate_in_app_purchase_event(
	store: String, 
	currency: String, 
	amount: float, 
	item_id: String, 
	category: String,
	receipt: String,
	signature: String = ""
) -> void:
	if _plugin_singleton:
		if _platform == "Android":
			_plugin_singleton.ValidateGoogleInAppPurchaseEvent(
				store, 
				currency, 
				amount, 
				item_id, 
				category, 
				receipt, 
				signature
			)
		elif _platform == "iOS":
			_plugin_singleton.ValidateiOSInAppPurchaseEvent(
				store, 
				currency, 
				amount, 
				item_id, 
				category, 
				receipt
			)


func load_remote_configs() -> void:
	if _plugin_singleton:
		_plugin_singleton.LoadRemoteConfigs()


func has_remote_configs_been_set() -> bool:
	if _plugin_singleton:
		return _plugin_singleton.HasRemoteConfigsBeenSet()
	return false


func get_remote_config(key: String, default_value := "") -> String:
	if _plugin_singleton:
		return _plugin_singleton.RetrieveRemoteConfigValue(key, default_value)
	return default_value


func get_user_id() -> String:
	if _plugin_singleton:
		return _plugin_singleton.GetUserID()
	return ""


func _on_remote_config_load_complete(status: bool) -> void:
	log_info("Remote configs load complete: %s" % str(status))
	remote_config_load_complete.emit(status)


func _on_in_app_purchase_validated(purchase_result: Dictionary) -> void:
	log_info("Inapp purchase result: %s" % str(purchase_result))
	in_app_purchase_validated.emit(ByteBrewPurchaseResult.create(purchase_result))


static func log_error(message: String) -> void:
	push_error("%s: %s" % [PLUGIN_SINGLETON_NAME, message])


static func log_warn(message: String) -> void:
	push_warning("%s: %s" % [PLUGIN_SINGLETON_NAME, message])


static func log_info(message: String) -> void:
	print_rich("[color=lime]%s: INFO: %s[/color]" % [PLUGIN_SINGLETON_NAME, message])

