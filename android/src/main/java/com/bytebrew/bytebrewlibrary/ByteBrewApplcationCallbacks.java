package com.bytebrew.bytebrewlibrary;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

public class ByteBrewApplcationCallbacks implements Application.ActivityLifecycleCallbacks {
	private boolean ResumUserSessionOnStart = false;

	public static boolean isInForeground = false;

	public static boolean isInBackground = false;

	private int activitiesStarted = 0;

	private Activity prevActivity = null;

	public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
	}

	public void onActivityStarted(Activity activity) {
		if (!activity.isTaskRoot() && this.activitiesStarted == 0) {
			this.activitiesStarted = 1;
			if (this.prevActivity == null)
				this.activitiesStarted++;
		} else {
			this.activitiesStarted++;
		}
		this.prevActivity = activity;
		if (this.ResumUserSessionOnStart) {
			isInBackground = false;
			isInForeground = true;
			this.ResumUserSessionOnStart = false;
			ByteBrewHandler.UserResumed();
		}
	}

	public void onActivityResumed(Activity activity) {
	}

	public void onActivityPaused(Activity activity) {
	}

	public void onActivityStopped(Activity activity) {
		this.activitiesStarted--;
		if (this.activitiesStarted <= 0) {
			this.activitiesStarted = 0;
			Log.d("ByteBrew", "No More Activities");
			isInBackground = true;
			isInForeground = false;
			ByteBrewHandler.CreateSessionEndEvent();
			this.ResumUserSessionOnStart = true;
		}
	}

	public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
	}

	public void onActivityDestroyed(Activity activity) {
	}
}
