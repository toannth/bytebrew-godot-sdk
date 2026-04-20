#
# © 2026-present https://github.com/ByteBrewIO
#

class_name ByteBrewPurchaseResult extends RefCounted

var purchase_valid: bool
var purchase_processed: bool
var item_id: String
var validation_time: String
var message: String


func _init(data: Dictionary):
    purchase_valid = data.get("purchase_valid", false)
    purchase_processed = data.get("purchase_processed", false)
    item_id = data.get("item_id", "")
    validation_time = data.get("validation_time", "")
    message = data.get("message", "")


static func create(data: Dictionary) -> ByteBrewPurchaseResult:
    return ByteBrewPurchaseResult.new(data)