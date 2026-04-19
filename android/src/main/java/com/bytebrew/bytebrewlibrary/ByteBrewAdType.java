package com.bytebrew.bytebrewlibrary;

public enum ByteBrewAdType {
	Interstitial, Reward, Banner;

	public String GetName() {
		switch (this) {
			case Interstitial:
				return "Interstitial";
			case Reward:
				return "Reward";
			case Banner:
				return "Banner";
		}
		return null;
	}
}
