package com.duke.dbus2.util;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;

/**
 * author: duke
 * dateTime: 2019-04-05 16:15
 * description:
 */
public class DUtils2 {

    public static String trim(String text) {
        if (text == null) {
            return null;
        }
        return text.trim();
    }

    public static boolean isNull(Object object) {
        return object == null;
    }

    public static boolean isEmpty(String text) {
        return text == null || "".equals(text.trim()) || text.trim().length() == 0;
    }


    public static boolean isEmpty(List list) {
        return list == null || list.isEmpty();
    }

    public static boolean isEmpty(Map map) {
        return map == null || map.isEmpty();
    }


    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(WeakReference weakReference) {
        return weakReference == null || weakReference.get() == null;
    }

    public static int stringToInt(String number) {
        return stringToInt(number, 0);
    }

    public static int stringToInt(String number, int defaultValue) {
        int result = defaultValue;
        try {
            result = Integer.parseInt(number);
        } catch (Exception e) {
            e.printStackTrace();
            DLog2.logD(e.getMessage());
        }
        return result;
    }

    public static long stringToLong(String number) {
        return stringToLong(number, 0L);
    }

    public static long stringToLong(String number, long defaultValue) {
        long result = defaultValue;
        try {
            result = Long.parseLong(number);
        } catch (Exception e) {
            e.printStackTrace();
            DLog2.logD(e.getMessage());
        }
        return result;
    }

    public static float stringToFloat(String number) {
        return stringToFloat(number, 0.0F);
    }

    public static float stringToFloat(String number, float defaultValue) {
        float result = defaultValue;
        try {
            result = Float.parseFloat(number);
        } catch (Exception e) {
            e.printStackTrace();
            DLog2.logD(e.getMessage());
        }
        return result;
    }

    public static double stringToDouble(String number) {
        return stringToDouble(number, 0.0D);
    }

    public static double stringToDouble(String number, double defaultValue) {
        double result = defaultValue;
        try {
            result = Double.parseDouble(number);
        } catch (Exception e) {
            e.printStackTrace();
            DLog2.logD(e.getMessage());
        }
        return result;
    }

}
