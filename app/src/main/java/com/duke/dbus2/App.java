package com.duke.dbus2;

import android.app.Activity;
import android.app.Application;

import java.lang.ref.WeakReference;

/**
 * author: duke
 * version: 1.0
 * dateTime: 2019-04-05 19:45
 * description:
 */
public class App extends Application {
    private static App app;

    private WeakReference<Activity> activityRef;

    public void setActivity(Activity activity) {
        this.activityRef = new WeakReference<>(activity);
    }

    public Activity getActivity() {
        if (activityRef != null && activityRef.get() != null) {
            return activityRef.get();
        }
        return null;
    }

    public static App getApp() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }
}
