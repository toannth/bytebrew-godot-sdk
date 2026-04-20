//
// © 2026-present https://github.com/ByteBrewIO
//

package org.godotengine.plugin.bytebrew;

import org.godotengine.godot.Dictionary;

import com.bytebrew.bytebrewlibrary.ByteBrewAdType;
import com.bytebrew.bytebrewlibrary.ByteBrewProgressionType;
import com.bytebrew.bytebrewlibrary.ByteBrewPurchaseResult;

class Converters {
    private Converters() {}

    public static ByteBrewProgressionType toProgressionType(int progressionType) {
		switch (progressionType) {
			case 0:
				return ByteBrewProgressionType.Started;
			case 1:
				return ByteBrewProgressionType.Completed;
			default:
				return ByteBrewProgressionType.Failed;
		}
	}
	
	public static ByteBrewAdType toAdType(int adType) {
		switch (adType) {
			case 0:
				return ByteBrewAdType.Interstitial;
			case 1:
				return ByteBrewAdType.Reward;
			default:
				return ByteBrewAdType.Banner;
		}
	}

    public static Dictionary toGodotDictionary(ByteBrewPurchaseResult purchaseResult) {
        Dictionary dict = new Dictionary();

        dict.put("purchase_valid", purchaseResult.isPurchaseValid());
        dict.put("purchase_processed", purchaseResult.isPurchaseProcessed());
        dict.put("item_id", purchaseResult.getItemID());
        dict.put("validation_time", purchaseResult.getValidationTime());
        dict.put("message", purchaseResult.getMessage());

        return dict;
    }
}
