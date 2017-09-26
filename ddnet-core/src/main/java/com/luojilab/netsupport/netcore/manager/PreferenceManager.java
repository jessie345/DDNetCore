package com.luojilab.netsupport.netcore.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.luojilab.netsupport.utils.NetCoreInitializer;

public class PreferenceManager {

    private static final String TAG = PreferenceManager.class.getSimpleName();

    /**
     * 默认偏好文件名
     */
    private static final String DEFAULT_SHARED_PREFERENCES_NAME = "net_core_shared_prefer";
    /**
     * 访问为私有模式
     */
    private static final int DEFAULT_FILE_MODE = Context.MODE_PRIVATE;

    /*********
     * for save preference value and clear
     ***********/

    public static boolean putInt(String key, int value) {
        Context context = NetCoreInitializer.getInstance().getAppContext();
        if (context == null) return false;

        SharedPreferences sp = context.getSharedPreferences(
                DEFAULT_SHARED_PREFERENCES_NAME, DEFAULT_FILE_MODE);
        return sp.edit().putInt(key, value).commit();
    }


    public static boolean putLong(String key, long value) {
        Context context = NetCoreInitializer.getInstance().getAppContext();
        if (context == null) return false;

        SharedPreferences sp = context.getSharedPreferences(
                DEFAULT_SHARED_PREFERENCES_NAME, DEFAULT_FILE_MODE);
        return sp.edit().putLong(key, value).commit();
    }


    public static boolean putString(String key, String value) {
        Context context = NetCoreInitializer.getInstance().getAppContext();
        if (context == null) return false;

        SharedPreferences sp = context.getSharedPreferences(
                DEFAULT_SHARED_PREFERENCES_NAME, DEFAULT_FILE_MODE);
        return sp.edit().putString(key, value).commit();
    }


    public static boolean putBoolean(String key, boolean value) {
        Context context = NetCoreInitializer.getInstance().getAppContext();
        if (context == null) return false;

        SharedPreferences sp = context.getSharedPreferences(
                DEFAULT_SHARED_PREFERENCES_NAME, DEFAULT_FILE_MODE);
        return sp.edit().putBoolean(key, value).commit();
    }


    public static void clearAll() {
        Context context = NetCoreInitializer.getInstance().getAppContext();
        if (context == null) return;

        SharedPreferences sp = context.getSharedPreferences(
                DEFAULT_SHARED_PREFERENCES_NAME, DEFAULT_FILE_MODE);
        sp.edit().clear().commit();
    }


    /***********
     * for get preference value
     *************/

    public static int getIntValue(String key) {
        return getIntValue(key, 0);
    }


    public static int getIntValue(String key, int defaultValue) {
        Context context = NetCoreInitializer.getInstance().getAppContext();
        if (context == null) return defaultValue;

        SharedPreferences sp = context.getSharedPreferences(
                DEFAULT_SHARED_PREFERENCES_NAME, DEFAULT_FILE_MODE);
        return sp.getInt(key, defaultValue);
    }


    public static long getLongValue(String key) {
        return getLongValue(key, 0);
    }


    public static long getLongValue(String key, long defaultValue) {
        Context context = NetCoreInitializer.getInstance().getAppContext();
        if (context == null) return defaultValue;

        SharedPreferences sp = context.getSharedPreferences(
                DEFAULT_SHARED_PREFERENCES_NAME, DEFAULT_FILE_MODE);
        return sp.getLong(key, defaultValue);
    }


    public static boolean getBooleanValue(String key) {
        Context context = NetCoreInitializer.getInstance().getAppContext();
        if (context == null) return false;

        SharedPreferences sp = context.getSharedPreferences(
                DEFAULT_SHARED_PREFERENCES_NAME, DEFAULT_FILE_MODE);
        return sp.getBoolean(key, false);
    }


    public static boolean getBooleanValue(String key, boolean defVale) {
        Context context = NetCoreInitializer.getInstance().getAppContext();
        if (context == null) return defVale;

        SharedPreferences sp = context.getSharedPreferences(
                DEFAULT_SHARED_PREFERENCES_NAME, DEFAULT_FILE_MODE);
        return sp.getBoolean(key, defVale);
    }


    public static String getStringValue(String key) {
        return getStringValue(key, null);
    }


    public static String getStringValue(String key, String defaultValue) {
        Context context = NetCoreInitializer.getInstance().getAppContext();
        if (context == null) return defaultValue;

        SharedPreferences sp = context.getSharedPreferences(
                DEFAULT_SHARED_PREFERENCES_NAME, DEFAULT_FILE_MODE);
        return sp.getString(key, defaultValue);
    }

}