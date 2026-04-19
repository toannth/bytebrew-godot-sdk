package com.bytebrew.bytebrewlibrary;

public enum ByteBrewProgressionType {
	Started, Completed, Failed;

	public String GetName() {
		switch (this) {
			case Started:
				return "Started";
			case Completed:
				return "Completed";
			case Failed:
				return "Failed";
		}
		return null;
	}
}
