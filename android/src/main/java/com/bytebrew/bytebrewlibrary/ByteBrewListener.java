package com.bytebrew.bytebrewlibrary;

import android.app.Application;

class ByteBrewListener {
	public static void CreateListeners(Application application) {
		application.registerActivityLifecycleCallbacks(new ByteBrewApplcationCallbacks());
	}
}
