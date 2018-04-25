package com.xinyi.touhang.utils;

/**
 * Created by Niu on 2016/12/7.
 */


import android.content.Context;
import android.content.SharedPreferences;

public class SpUtilsForDownLoad {

    private SharedPreferences sp;
    private static SpUtilsForDownLoad instance;

    private SpUtilsForDownLoad(Context context) {
        sp = context.getSharedPreferences("download_sp", Context.MODE_PRIVATE);
    }

    public static synchronized SpUtilsForDownLoad getInstance(Context context) {
        if (instance == null) {
            instance = new SpUtilsForDownLoad(context.getApplicationContext());
        }
        return instance;
    }

    public SpUtilsForDownLoad putInt(String key, int value) {
        sp.edit().putInt(key, value).apply();
        return this;
    }

    public int getInt(String key, int dValue) {
        return sp.getInt(key, dValue);
    }

    public SpUtilsForDownLoad putLong(String key, long value) {
        sp.edit().putLong(key, value).apply();
        return this;
    }

    public long getLong(String key, Long dValue) {
        return sp.getLong(key, dValue);
    }

    public SpUtilsForDownLoad putFloat(String key, float value) {
        sp.edit().putFloat(key, value).apply();
        return this;
    }

    public Float getFloat(String key, Float dValue) {
        return sp.getFloat(key, dValue);
    }

    public SpUtilsForDownLoad putBoolean(String key, boolean value) {
        sp.edit().putBoolean(key, value).apply();
        return this;
    }

    public Boolean getBoolean(String key, boolean dValue) {
        return sp.getBoolean(key, dValue);
    }

    public SpUtilsForDownLoad putString(String key, String value) {
        sp.edit().putString(key, value).apply();
        return this;
    }

    public String getString(String key, String dValue) {
        return sp.getString(key, dValue);
    }

    public void remove(String key) {
        if (isExist(key)) {
            SharedPreferences.Editor editor = sp.edit();
            editor.remove(key);
            editor.apply();
        }
    }

    public boolean isExist(String key) {
        return sp.contains(key);
    }
}