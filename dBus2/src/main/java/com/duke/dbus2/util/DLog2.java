package com.duke.dbus2.util;

import android.util.Log;

import com.duke.dbus2.core.DBus2;

/**
 * author: duke
 * dateTime: 2019-04-05 16:15
 * description:
 */
public class DLog2 {

    private static boolean IS_DEBUG = false;

    public static void isDebug(boolean debug) {
        IS_DEBUG = debug;
    }

    public static void logD(String msg) {
        if (!IS_DEBUG) {
            return;
        }
        if (DUtils2.isEmpty(msg)) {
            return;
        }
        Log.d(DBus2.TAG, msg);
    }

    public static void logTime(long startTime, String methodName) {
        if (!IS_DEBUG) {
            return;
        }
        logD(methodName + " time consuming " + (System.currentTimeMillis() - startTime) + " ms.");
    }

}
